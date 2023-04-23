package docrob.ymirspringblog;

import docrob.ymirspringblog.models.*;
import docrob.ymirspringblog.repositories.FriendRepository;
import docrob.ymirspringblog.repositories.PostRepository;
import docrob.ymirspringblog.repositories.UserRepository;
import docrob.ymirspringblog.services.S3Helper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class YmirSpringBlogApplicationTests {
    @Autowired
    private PostRepository postDao;

    private final int SIGNATURE_DURATION = 10;

    @Autowired
    private S3Helper s3Helper;

    @Autowired
    private FriendRepository friendDao;

    @Autowired
    private UserRepository userDao;

    @Test
    public void friendPosts() {
        List<PostDTO> posts = postDao.findMyFriendsPosts(1L);
        System.out.println(posts);
    }

    @Test
    public void foo() {
        postDao.deleteById(3L);
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
                .bucket(s3Helper.BUCKET)
                .key(key)
                .build();

        CreateMultipartUploadResponse response = s3.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = response.uploadId();

        System.out.println("uploadId: " + uploadId);

        // chop a 13 MB image into 3 pieces: 2 5MB pieces, and one 3 MB piece
        File file = null;
        FileInputStream fiStream = null;
        BufferedInputStream biStream = null;
        List<MyFilePart> partsInfo = new ArrayList<>();
        int partNum = 1;
        int startIndex = 0;
        try {
            file = new File("/Users/markrobinson/Desktop/blackmarble.png");
            fiStream = new FileInputStream(file);
            biStream = new BufferedInputStream(fiStream);

            int MAX_PART_SIZE = 5_242_880; // aws wants the binary value for 5MB which is not 5 mil so I just use 6 mil
            int numBytesRead = MAX_PART_SIZE;
            while(numBytesRead == MAX_PART_SIZE) {
                byte[] bytes = new byte[MAX_PART_SIZE];
                numBytesRead = biStream.read(bytes, 0, MAX_PART_SIZE);
                if (numBytesRead < 1) {
                    break;
                }
                // if bytes is less than 5MB then resize it
                if(numBytesRead < MAX_PART_SIZE) {
                    byte [] newBytes = new byte[numBytesRead];
                    System.arraycopy(bytes, 0, newBytes, 0, numBytesRead);
                    bytes = newBytes;
                }

                // calc the part's MD5 checksum
                String checksum = getMD5(bytes);

                System.out.println("Read " + numBytesRead + " bytes, md5 checksum " + checksum);

                // get the multipart upload url
                // try to get a multipart upload presigned url for piece 1
                UploadPartRequest uRequest = UploadPartRequest.builder()
                        .bucket(s3Helper.BUCKET)
                        .key(key)
                        .uploadId(uploadId)
                        .partNumber(partNum)
                        .build();
                UploadPartPresignRequest presignURequest = UploadPartPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(SIGNATURE_DURATION))
                        .uploadPartRequest(uRequest)
                        .build();
                PresignedUploadPartRequest presignedRequest = presigner.presignUploadPart(presignURequest);
                String url = presignedRequest.url().toString();
                System.out.println("upload part 1 presigned url: " + url);


                // send the part to aws s3 as a multipart
                // TODO: retry x times to send it
                uploadMultipartToPresignedURL(presignedRequest.url(), bytes, partNum, checksum);

                // save the part info in the list
                partsInfo.add(new MyFilePart(partNum, checksum));

                startIndex += numBytesRead;
                partNum++;
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(fiStream != null) {
                try {
                    fiStream.close();
                } catch (IOException e) {
                    // don't care
                }
            }
            if(biStream != null) {
                try {
                    biStream.close();
                } catch (IOException e) {
                    // don't care
                }
            }
        }

        System.out.println(partsInfo);

        // eventually part responses will be processed on js side
        // and js will have to give us back a list of the etags with their respective part numbers
        // complete the multipart upload
        List<CompletedPart> completedParts = new ArrayList<>(); // retrieve the list of completed parts
        for (MyFilePart partInfo : partsInfo) {
            completedParts.add(CompletedPart.builder().eTag(partInfo.getMd5Hash()).partNumber(partInfo.getPartNum()).build());
        }

        try {
            CompleteMultipartUploadRequest request = CompleteMultipartUploadRequest.builder()
                    .bucket(s3Helper.BUCKET)
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

    }

    private void uploadMultipartToPresignedURL(URL url, byte[] bytes, int partNum, String checksum) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","text/plain");
            connection.setRequestMethod("PUT");

//            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
//            out.write(bytes);
//            out.close();
            connection.getOutputStream().write(bytes);
            connection.getOutputStream().close();

            System.out.println("PUT HTTP response code is " + connection.getResponseCode());
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String s;
//            while((s = br.readLine()) != null) {
//                System.out.println(s);
//            }
//            br.close();

            // Download the result of executing the request
//            try (InputStream content = connection.getInputStream()) {
//                System.out.println("Service returned response: ");
//                IoUtils.copy(content, System.out);
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static String getMD5(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input);
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "crap";
    }
}
