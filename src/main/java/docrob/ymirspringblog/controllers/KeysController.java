package docrob.ymirspringblog.controllers;

import docrob.ymirspringblog.config.APIKeys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@AllArgsConstructor
@Controller
public class KeysController {
    private APIKeys apiKeys;

    @GetMapping(value = "/keys.js", produces = "text/javascript")
    @ResponseBody
    public String getKeys() {
        return "const TALKJS_APIKEY = '" + apiKeys.TALKJS_APP_KEY + "';\n";
    }
}