package docrob.ymirspringblog;

import docrob.ymirspringblog.models.Friend;
import docrob.ymirspringblog.models.User;
import docrob.ymirspringblog.repositories.FriendRepository;
import docrob.ymirspringblog.repositories.PostRepository;
import docrob.ymirspringblog.repositories.UserRepository;
import docrob.ymirspringblog.services.S3Helper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
class YmirSpringBlogApplicationTests {
    @Autowired
    private PostRepository postRepository;

    private final int SIGNATURE_DURATION = 10;

    @Autowired
    private S3Helper s3Helper;

    @Autowired
    private FriendRepository friendDao;

    @Autowired
    private UserRepository userDao;

    @Test
    public void foo() {
        postRepository.deleteById(3L);
    }

    @Test
    void uploadFileViaPresignedURL() {
//        s3Helper.foo();
    }

    @Test
    public void friends() {
        List<Friend> friends = friendDao.findAll();
        for(Friend friend : friends) {
            System.out.println(friend.getFriend1().getUsername() + " -> " + friend.getFriend2().getUsername());
        }
    }

    @Test
    public void checkJethroFollowers() {
        User jethro = userDao.findById(11L).get();
        System.out.println(jethro.getFollowers().size());
        System.out.println(jethro.getFollowers().get(0).getUsername());
    }

//    @Test
//    public void downloadPresignedURL() {
//        String key = "d9f9a510-dd70-41c2-944c-1f5330ad7f28";
//
//        System.out.println(s3Helper.getDownloadPresignedURL(key));
//    }

    @Test
    public void multiPart() {
        String key = "1001123123213";
        /*
        AmazonS3 s3 = s3Helper.getS3Client();

        // get an upload id for a multipart upload
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        CreateMultipartUploadResponse response = s3.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = response.uploadId();
        System.out.println(uploadId);
        */

        S3Client s3 = s3Helper.getS3Client();

        S3Presigner presigner = s3Helper.getPresigner();

        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(S3Helper.BUCKET)
                .key(key)
                .build();

        CreateMultipartUploadResponse response = s3.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = response.uploadId();

        System.out.println("uploadId: " + uploadId);

        // try to get a multipart upload presigned url for part 1
        UploadPartRequest uRequest = UploadPartRequest.builder()
                .bucket(S3Helper.BUCKET)
                .key(key)
                .uploadId(uploadId)
                .partNumber(1)
                .build();
        UploadPartPresignRequest presignURequest = UploadPartPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(SIGNATURE_DURATION))
                .uploadPartRequest(uRequest)
                .build();
        PresignedUploadPartRequest presignedRequest = presigner.presignUploadPart(presignURequest);
        String url = presignedRequest.url().toString();
        System.out.println("upload part 1 presigned url: " + url);


        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) presignedRequest.url().openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","text/plain");
            connection.setRequestMethod("PUT");

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write("This asdf;weoinvd alksnqwwpodkvdfw9qwjdjvkf hsdfiuawhazdfs- text was uploaded as an object by using a presigned URL.");
            out.close();

            System.out.println("PUT HTTP response code is " + connection.getResponseCode());
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String s;
//            while((s = br.readLine()) != null) {
//                System.out.println(s);
//            }
//            br.close();

            // Download the result of executing the request
            try (InputStream content = connection.getInputStream()) {
                System.out.println("Service returned response: ");
                IoUtils.copy(content, System.out);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        // eventually part responses will be processed on js side
        // and js will have to give us back a list of the etags with their respective part numbers
        List<UploadPartResponse> uploadPartResponses = new ArrayList<>();

        // complete the multipart upload
        List<CompletedPart> completedParts = new ArrayList<>(); // retrieve the list of completed parts
        for (int i = 0; i < uploadPartResponses.size(); i++) {
            int partNumber = i + 1;
            UploadPartResponse upResponse = uploadPartResponses.get(i);
            completedParts.add(CompletedPart.builder().eTag(upResponse.eTag()).partNumber(partNumber).build());
        }

        try {
            CompleteMultipartUploadRequest request = CompleteMultipartUploadRequest.builder()
                    .bucket(S3Helper.BUCKET)
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build();
            CompleteMultipartUploadResponse cResponse = s3.completeMultipartUpload(request);
            System.out.println("Object key: " + cResponse.key());
            // an eTag is just a string
            System.out.println("ETag: " + cResponse.eTag());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s3.close();
        }


         */
    }

    @Test
    public void crapHead() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://www.google.com");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","text/plain");
            connection.setRequestMethod("GET");

//            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
//            out.write("This asdf;weoinvd alksnqwwpodkvdfw9qwjdjvkf hsdfiuawhazdfs- text was uploaded as an object by using a presigned URL.");
//            out.close();

            System.out.println("GET HTTP response code is " + connection.getResponseCode());
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String s;
//            while((s = br.readLine()) != null) {
//                System.out.println(s);
//            }
//            br.close();

            // Download the result of executing the request
            try (InputStream content = connection.getInputStream()) {
                System.out.println("Service returned response: ");
                IoUtils.copy(content, System.out);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
