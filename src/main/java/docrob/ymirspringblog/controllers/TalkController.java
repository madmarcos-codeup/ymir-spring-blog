package docrob.ymirspringblog.controllers;

import docrob.ymirspringblog.models.User;
import docrob.ymirspringblog.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/talk")
public class TalkController {

    private UserRepository userDao;

    @GetMapping
    public String friendList(Model model) {
        List<User> friends = userDao.findAll();
        model.addAttribute("friends", friends);
        return "/talk/index";
    }

    @GetMapping(value = "/show")
    public String showTalk() {
        return "/talk/show";
    }

    @GetMapping(value = "/me", produces = "application/json")
    @ResponseBody
    public User getMyTalkInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }

    @GetMapping(value = "/with/{friendId}")
    public String talkWith(@PathVariable Long friendId, Model model) {
        User friend = userDao.findById(friendId).get();
        model.addAttribute("friend", friend);
        return "/talk/show";
    }

    @GetMapping(value = "/{userId}", produces = "application/json")
    @ResponseBody
    public User getMyTalkInfo(@PathVariable Long userId) {
        User user = userDao.findById(userId).get();
        return user;
    }

}