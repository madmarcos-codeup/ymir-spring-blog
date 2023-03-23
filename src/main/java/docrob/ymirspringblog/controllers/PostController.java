package docrob.ymirspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping(path = "/posts")
public class PostController {
    @GetMapping
    public String allPosts() {
        return "posts index page";
    }

    @GetMapping("/{id}")
    public String onePost(@PathVariable long postId) {
        return "view an individual post";
    }

    @GetMapping("/create")
    public String showCreatePostForm() {
        return "view the form for creating a post";
    }

    @PostMapping("/create")
    public String addPost() {
        return "create a new post";
    }
}
