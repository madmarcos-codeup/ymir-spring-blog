package docrob.ymirspringblog.controllers;

import docrob.ymirspringblog.models.Friend;
import docrob.ymirspringblog.models.User;
import docrob.ymirspringblog.repositories.FriendRepository;
import docrob.ymirspringblog.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Controller
public class UserController {
    private UserRepository userDao;
    private PasswordEncoder passwordEncoder;

    private FriendRepository friendDao;

    @GetMapping(value = "/me/friends", produces = "application/json")
    @ResponseBody
    public List<User> getFriends() {
        List<User> friends = new ArrayList<>();
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Friend> friendsRecords = friendDao.findAll();
        for(Friend friendRec : friendsRecords) {
            if(friendRec.getFriend1().getId() == me.getId()) {
                friends.add(friendRec.getFriend2());
            }
        }
        System.out.println(friends);
        return friends;
    }

    @GetMapping("/sign-up")
    public String showSignupForm(Model model){
        model.addAttribute("user", new User());
        return "users/sign-up";
    }

    @PostMapping("/sign-up")
    public String saveUser(@ModelAttribute User user){
        String hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);
        userDao.save(user);
        return "redirect:/login";
    }
}

