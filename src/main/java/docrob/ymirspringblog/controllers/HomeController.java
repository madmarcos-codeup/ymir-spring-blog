package docrob.ymirspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@ResponseBody
@RequestMapping(path = "/home")
public class HomeController {
    @GetMapping
    public String welcome() {
        return "home";
    }
}
