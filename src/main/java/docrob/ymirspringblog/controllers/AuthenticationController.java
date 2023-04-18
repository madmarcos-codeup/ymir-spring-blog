package docrob.ymirspringblog.controllers;

import docrob.ymirspringblog.misc.LoginFunctions;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userNameLabel", LoginFunctions.getLoggedInUserNameMenuLabel());

        model.addAttribute("selectedPage", "login");
        return "users/login";
    }

    /*
    this is a manual version of logging out
    that can be called via GET /my/logout
     */
    @GetMapping("/my/logout")
    public String manualLogout(HttpServletRequest request) {
        try {
            request.logout();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/posts";
    }

    @GetMapping("/denied")
    public String denied(Model model) {
        return "denied";
    }


}

