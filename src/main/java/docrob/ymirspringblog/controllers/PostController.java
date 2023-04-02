package docrob.ymirspringblog.controllers;

import docrob.ymirspringblog.models.Post;
import docrob.ymirspringblog.models.User;
import docrob.ymirspringblog.repositories.PostRepository;
import docrob.ymirspringblog.repositories.UsersRepository;
import docrob.ymirspringblog.services.EmailService;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/posts")
public class PostController {
    private final PostRepository postDao;
    private final UsersRepository userDao;
    private final EmailService emailService;

    @GetMapping
    public String allPosts(@RequestParam @Nullable String search, Model model) {
        if(search != null) {
            model.addAttribute("posts", postDao.findByTitle(search));
        } else {
            model.addAttribute("posts", postDao.findAll());
        }
        return "posts/index";
    }

    @GetMapping("/{postId}")
    public String onePost(@PathVariable long postId, Model model) {
        Post post = postDao.findById(postId).get();
        model.addAttribute("post", post);
        return "posts/show";
    }

    @GetMapping("/{postId}/edit")
    public String editOnePost(@PathVariable long postId, Model model) {
        Post post = postDao.findById(postId).get();
        model.addAttribute("post", post);
        return "posts/create";
    }

    @GetMapping("/{postId}/delete")
    public String deleteOnePost(@PathVariable long postId, Model model) {
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
        Post post = new Post();
        post.setTitle("Test post");
        post.setBody("Test post body");

        User creator = userDao.findById(1L).get();
        post.setCreator(creator);

        model.addAttribute("post", post);
        return "posts/create";
    }

    @PostMapping("/create")
    public String addPost(@ModelAttribute Post post) {
//        System.out.println(post);

        // set the creator to bob w id 1 for now
        User creator = userDao.findById(1L).get();
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
