package docrob.ymirspringblog.services;

/*
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
*/
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
//import jakarta.mail.Multipart;
//import org.springframework.http.HttpMethod;
//import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
//import software.amazon.awssdk.services.s3.model.GetObjectRequest;
//import software.amazon.awssdk.services.s3.model.MultipartUpload;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.presigner.S3Presigner;
//import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
//import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
//import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
//import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class S3Helper {
    private final int SIGNATURE_DURATION = 5; // in minutes

    public static final Region REGION = Region.US_EAST_2;

    public static String BUCKET = "ymir-demo";

//    @Autowired
//    private AmazonS3 s3Client;

    public S3Client getS3Client() {
        Region region = Region.US_EAST_2;
        return S3Client.builder()
                .region(region)
                .build();
    }
//    public AmazonS3 getS3Client() {
//        return s3Client;
//    }


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

    public String getDownloadPresignedURL(String key) {
        java.util.Date expiration = getSignatureExpiration();

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(SIGNATURE_DURATION))
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = getPresigner().presignGetObject(presignRequest);
        return presignedRequest.url().toString();
        // Generate the presigned URL.
//        log.info("Generating pre-signed URL.");
//        GeneratePresignedUrlRequest generatePresignedUrlRequest =
//                new GeneratePresignedUrlRequest(bucket, key)
//                        .withMethod(HttpMethod.GET)
//                        .withExpiration(expiration);
//        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
//        return url.toString();

    }

    private Date getSignatureExpiration() {
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = Instant.now().toEpochMilli();
        expTimeMillis += 1000 * 60 * SIGNATURE_DURATION; // default to 5 minute expiration
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    public PresignUrlInfo getUploadPresignedURL(String mimeType) {
        PresignUrlInfo info = new PresignUrlInfo();
        try {
            String key = UUID.randomUUID().toString();
//            String imageLocation = "/Users/markrobinson/Desktop/dude.gif";
//            byte[] pic = Files.readAllBytes(Paths.get(imageLocation));
            //        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();

            S3Presigner presigner = getPresigner();

//            signBucket(presigner, bucketName, keyName, pic);
            String url =  generatePresignedURL(presigner, BUCKET, key, mimeType);
            info.setUrl(url);
            info.setKey(key);
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

    public S3Presigner getPresigner() {
        EnvironmentVariableCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();
        S3Presigner presigner = S3Presigner.builder()
                .region(REGION)
                .credentialsProvider(credentialsProvider)
                .build();
        return presigner;
    }

    public void foo(String key, String uploadId) {
        try {
//            GeneratePresignedUrlRequest request;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}