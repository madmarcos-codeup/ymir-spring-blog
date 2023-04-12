package docrob.ymirspringblog.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@AllArgsConstructor
@Controller
public class JsonController {
    @GetMapping(value = "/keys.js", produces = "text/javascript")
    @ResponseBody
    public String getKeys() {
        return "const msg = 'help me!';";
    }
}