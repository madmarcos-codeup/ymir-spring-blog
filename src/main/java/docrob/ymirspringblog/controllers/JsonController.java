package docrob.ymirspringblog.controllers;

import docrob.ymirspringblog.config.APIKeys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ObjectInputFilter;

@AllArgsConstructor
@Controller
public class JsonController {

    @GetMapping(value = "/keys.js", produces = "text/javascript")
    @ResponseBody
    public String getKeys() {
        return "const msg = 'help me!';" +
            "const TALKJS_APIKEY = " + APIKeys.getTalkJSAppKey() + ";";
    }
}