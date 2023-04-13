package docrob.ymirspringblog.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class S3Helper {
    private final int SIGNATURE_DURATION = 10;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucketName;

//    public String getSignedURL(String fileName) {
//        java.util.Date expiration = new java.util.Date();
//        long expTimeMillis = Instant.now().toEpochMilli();
//        expTimeMillis += 1000 * 60 * 5; // default to 5 minute expiration
//        expiration.setTime(expTimeMillis);
//
//        // Generate the presigned URL.
//        log.info("Generating pre-signed URL.");
//        GeneratePresignedUrlRequest generatePresignedUrlRequest =
//                new GeneratePresignedUrlRequest(bucket, fileName)
//                        .withMethod(HttpMethod.GET)
//                        .withExpiration(expiration);
//        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
//        return url.toString();
//    }

    public String getDownloadPresignedURL(String keyName) {
        EnvironmentVariableCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();
        Region region = Region.US_EAST_2;
        S3Presigner presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
        String url = presignedGetObjectRequest.url().toString();
        return url;
    }

    public PresignUrlInfo getUploadPresignedURL(String mimeType) {
        PresignUrlInfo info = new PresignUrlInfo();
        try {
            String keyName = UUID.randomUUID().toString();
//            String imageLocation = "/Users/markrobinson/Desktop/dude.gif";
//            byte[] pic = Files.readAllBytes(Paths.get(imageLocation));
            //        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
            EnvironmentVariableCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();
            Region region = Region.US_EAST_2;
            S3Presigner presigner = S3Presigner.builder()
                    .region(region)
                    .credentialsProvider(credentialsProvider)
                    .build();

//            signBucket(presigner, bucketName, keyName, pic);
            String url =  generatePresignedURL(presigner, bucketName, keyName, mimeType);
            info.setUrl(url);
            info.setKey(keyName);
            presigner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    //byte[] pic
    public String generatePresignedURL(S3Presigner presigner, String bucketName, String keyName, String mimeType) {
        String url = "";
        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .contentType(mimeType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(SIGNATURE_DURATION))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            url = presignedRequest.url().toString();

//            System.out.println("Presigned URL to upload a file to: " + myURL);
//            System.out.println("Which HTTP method needs to be used when uploading a file: " + presignedRequest.httpRequest().method());
//
//            // Upload content to the Amazon S3 bucket by using this URL.
//            URL url = presignedRequest.url();
//
//            // Create the connection and use it to upload the new object by using the presigned URL.
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);
//            connection.setRequestProperty("Content-Type", "image/gif");
//            connection.setRequestMethod("PUT");
//            connection.getOutputStream().write(pic);
//            connection.getResponseCode();
//            System.out.println("HTTP response code is " + connection.getResponseCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}