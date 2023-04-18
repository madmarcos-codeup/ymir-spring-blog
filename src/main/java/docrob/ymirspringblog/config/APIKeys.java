package docrob.ymirspringblog.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class APIKeys {

    @Value("${talkjs.appkey}")
    private static String talkJSAppKey;


    public static String getTalkJSAppKey() {
        return "tU84aRwM";
    }
}
