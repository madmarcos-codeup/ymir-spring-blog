package docrob.ymirspringblog;

import docrob.ymirspringblog.services.S3Helper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YmirSpringBlogApplicationTests {
    @Autowired
    private S3Helper s3Helper;

    @Test
    void uploadFileViaPresignedURL() {
//        s3Helper.foo();
    }

}
