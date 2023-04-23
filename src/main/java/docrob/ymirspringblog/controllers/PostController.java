package docrob.ymirspringblog.controllers;

import docrob.ymirspringblog.misc.LoginFunctions;
import docrob.ymirspringblog.models.Post;
import docrob.ymirspringblog.models.User;
import docrob.ymirspringblog.repositories.PostRepository;
import docrob.ymirspringblog.repositories.UserRepository;
import docrob.ymirspringblog.services.EmailService;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/posts")
public class PostController {
    private final PostRepository postDao;
    private final UserRepository userDao;
    private final EmailService emailService;

    @DeleteMapping("/{postId}")
    @ResponseBody
    public String deletePost(@PathVariable Long postId) {
        // make sure only the post's creator can delete it
        // 1. get the id of the currently logged in user
        // 2. fetch the post from the dao to be deleted
        // 3. if the post creator's id is different than the logged in user
        //      return a 403
        // 4. otherwise you are the creator so you can delete it
        userDao.deletePostById(postId);
        return "ok";
    }

    @GetMapping
    public String allPosts(@RequestParam @Nullable String search, Model model) {
//        model.addAttribute("myVar", "hello");

//        try {
//            URL url = new URL("https://www.themealdb.com/api/json/v1/1/lookup.php?i=53065");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);
//            connection.setRequestProperty("Content-Type","text/plain");
//            connection.setRequestMethod("GET");
////            connection.getOutputStream().write(pic);
//            connection.getResponseCode();
//            String jsonResponse = new String(connection.getInputStream().readAllBytes());
//            System.out.println("HTTP response code is " + connection.getResponseCode());
//            System.out.println(jsonResponse);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        model.addAttribute("userNameLabel", LoginFunctions.getLoggedInUserNameMenuLabel());
        model.addAttribute("userName", LoginFunctions.getLoggedInUserName());

        if(search != null) {
            model.addAttribute("posts", postDao.findByTitle(search));
        } else {
            model.addAttribute("posts", postDao.findAll());
        }
        model.addAttribute("selectedPage", "posts");
        return "posts/index";
    }

    @GetMapping("/{postId}")
    public String onePost(@PathVariable long postId, Model model) {
        model.addAttribute("userNameLabel", LoginFunctions.getLoggedInUserNameMenuLabel());

        Post post = postDao.findById(postId).get();
        if(post == null) {
            return "redirect:/error";
        }
        model.addAttribute("post", post);
        return "posts/show";
    }

    @GetMapping("/{postId}/edit")
    public String editOnePost(@PathVariable long postId, Model model) {
        String loggedInUserName = LoginFunctions.getLoggedInUserName();
        model.addAttribute("userNameLabel", loggedInUserName);

        Post post = postDao.findById(postId).get();

        if(!loggedInUserName.equals(post.getCreator().getName())) {
            return "redirect:/denied";
        }
        model.addAttribute("title", "Edit Post");
        model.addAttribute("post", post);
        return "posts/create";
    }

    @GetMapping("/{postId}/delete")
    public String deleteOnePost(@PathVariable long postId, Model model) {
        String loggedInUserName = LoginFunctions.getLoggedInUserName();
        Post post = postDao.findById(postId).get();
        if(!loggedInUserName.equals(post.getCreator().getName())) {
            return "redirect:/denied";
        }

        postDao.deleteById(postId);
        return "redirect:/posts";
    }

    @PostMapping("/{postId}/edit")
    public String saveOnePost(@PathVariable long postId,
                              @ModelAttribute Post post) {
        // fetch original post from db
        Post originalPost = postDao.findById(postId).get();

        // set any info in post that is missing from the form
        post.setCreator(originalPost.getCreator());

        // save to dao
        postDao.save(post);
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/create")
    public String showCreatePostForm(Model model) {
        model.addAttribute("userNameLabel", LoginFunctions.getLoggedInUserNameMenuLabel());

        Post post = new Post();
        post.setTitle("Test post");
        post.setBody("Test post body");

        User creator = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        post.setCreator(creator);

        model.addAttribute("post", post);
        model.addAttribute("title", "Create Post");
        model.addAttribute("selectedPage", "posts-create");
        return "posts/create";
    }

    @PostMapping("/create")
    public String addPost(@ModelAttribute Post post) {
        // set the creator to bob w id 1 for now
        User creator = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        post.setCreator(creator);

        // save to dao
        postDao.save(post);

        emailService.prepareAndSend(post,
                "New post created",
                """
Hi there,

A new post was created listing you as the creator.

If this was you, then please disregard this email.
If this was not you, please start panicking.

Thank you,
    Your friendly neighborhood email robot
                        """);
        // redirect user somewhere
        return "redirect:/posts";
    }
}
