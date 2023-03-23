package docrob.ymirspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping(path = "/")
public class HomeController {
    @GetMapping
    public String homePage() {
        return "This is the landing page!";
    }
}
