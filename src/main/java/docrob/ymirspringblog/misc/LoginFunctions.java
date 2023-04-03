package docrob.ymirspringblog.misc;

import docrob.ymirspringblog.models.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoginFunctions {
    public static String getLoggedInUserName() {
        String name = "";
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            name = user.getName();
            if(name == null) {
                name = "";
            }
        } catch(Exception e) {

        }
        return name;
    }

    public static String getLoggedInUserNameMenuLabel() {
        String label = "Not logged in";
        String name = getLoggedInUserName();
        if(name != null && name.length() > 0) {
            label = "Logged in as " + name;
        }
        return label;
    }

}
