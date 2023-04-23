package docrob.ymirspringblog.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class APIKeys {

    @Value("${talkjs.appkey}")
    public String TALKJS_APP_KEY;

    @Value("${filestack.apikey}")
    public String FILESTACK_API_KEY;

}
