package docrob.ymirspringblog.controllers;

import docrob.ymirspringblog.misc.LoginFunctions;
import docrob.ymirspringblog.models.MyFile;
import docrob.ymirspringblog.models.Post;
import docrob.ymirspringblog.models.User;
import docrob.ymirspringblog.services.S3Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/files")
public class FileController {
    @Autowired
    private S3Helper s3Helper;

    @GetMapping("/upload")
    public String getUploadForm(Model model) {
        model.addAttribute("userNameLabel", LoginFunctions.getLoggedInUserNameMenuLabel());
        model.addAttribute("userName", LoginFunctions.getLoggedInUserName());

        MyFile file = new MyFile();
        file.setDescription("put stuff here");

//        User creator = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        post.setCreator(creator);

        model.addAttribute("myFile", file);
        model.addAttribute("selectedPage", "files-upload");

        return "files/upload";
    }

    @GetMapping(value = "/presign", produces = "application/json")
    @ResponseBody
    public String getPresignedURL(@RequestParam String mimeType, Model model) {
        if(mimeType == null) {
            throw new RuntimeException("ERROR: mimeType query parameter required!");
        }
        String url = s3Helper.getUploadPresignedURL(mimeType);
        System.out.println(url);
        return "{ \"url\" : \"" + url + "\"}";
    }
}