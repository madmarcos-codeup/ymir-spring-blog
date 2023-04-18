package docrob.ymirspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@ResponseBody
@RequestMapping(path = "/home")
public class HomeController {
    @GetMapping(value = "/hello", produces = "text/plain")
    @ResponseBody
    public String sayHello(@RequestParam(required = false) String name) {
        String response = "Hello ";
        if(name != null) {
            response += name;
        }
        return response;
    }

    @GetMapping
    public String welcome() {
        return "home";
    }
}
