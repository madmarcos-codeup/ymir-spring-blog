package docrob.ymirspringblog.controllers;

import docrob.ymirspringblog.misc.LoginFunctions;
import docrob.ymirspringblog.models.*;
import docrob.ymirspringblog.repositories.FileRepository;
import docrob.ymirspringblog.services.PresignUrlInfo;
import docrob.ymirspringblog.services.S3Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping(path = "/files")
public class FileController {
    @Autowired
    private S3Helper s3Helper;

    @Autowired
    private FileRepository fileDao;

    private final int SIGNATURE_DURATION = 10;

    @GetMapping
    public String all(Model model) {
        model.addAttribute("userNameLabel", LoginFunctions.getLoggedInUserNameMenuLabel());
        model.addAttribute("userName", LoginFunctions.getLoggedInUserName());

        model.addAttribute("selectedPage", "files");
        List<MyFile> files = fileDao.findAll();

        for (MyFile file : files) {
            String url = s3Helper.getDownloadPresignedURL(file.getCloudKey());
            file.setCloudUrl(url);
        }
        model.addAttribute("files", files);
        return "/files/index";
    }

    @PostMapping("/create")
    public String createFile(@ModelAttribute MyFile myFile) {
        fileDao.save(myFile);
        return "redirect:/files";
    }

    @GetMapping("/upload")
    public String getUploadForm(Model model) {
        model.addAttribute("userNameLabel", LoginFunctions.getLoggedInUserNameMenuLabel());
        model.addAttribute("userName", LoginFunctions.getLoggedInUserName());

        MyFile file = new MyFile();
        file.setDescription("put stuff here");

//        User creator = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        post.setCreator(creator);

        model.addAttribute("myFile", file);
        model.addAttribute("selectedPage", "files-upload");

        return "files/upload";
    }

    @GetMapping(value= "/multipart/start", produces = "application/json")
    @ResponseBody
    public MyMultiPartUpload startMultipart() {
        String key = UUID.randomUUID().toString();
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(S3Helper.BUCKET)
                .key(key)
                .build();

        CreateMultipartUploadResponse response = s3Helper.getS3Client().createMultipartUpload(createMultipartUploadRequest);
        MyMultiPartUpload upload = new MyMultiPartUpload(response.uploadId(), key);
        return upload;
    }

    @GetMapping(value = "/multipart/part", produces = "application/json")
    @ResponseBody
    public String getMultipartPresignedURL(@RequestParam(required = true) String uploadId,
        @RequestParam(required = true) String key,
        @RequestParam(required = true) String partNum) {

        UploadPartRequest uRequest = UploadPartRequest.builder()
                .bucket(S3Helper.BUCKET)
                .key(key)
                .uploadId(uploadId)
                .partNumber(Integer.parseInt(partNum))
                .build();
        UploadPartPresignRequest presignURequest = UploadPartPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(SIGNATURE_DURATION))
                .uploadPartRequest(uRequest)
                .build();
        PresignedUploadPartRequest presignedRequest = s3Helper.getPresigner().presignUploadPart(presignURequest);
        return "{\"url\":\"" + presignedRequest.url().toString() + "\"}";
    }

    @PostMapping(value="/multipart/complete", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String completeMultipart(@RequestBody MyMultipartComplete formData) {
        System.out.println(Arrays.toString(formData.getFileParts()));

        List<CompletedPart> completedParts = new ArrayList<>(); // retrieve the list of completed parts
        for (MyFilePart partInfo : formData.getFileParts()) {
            completedParts.add(CompletedPart.builder().eTag(partInfo.getMd5Hash()).partNumber(partInfo.getPartNum()).build());
        }

        try {
            CompleteMultipartUploadRequest request = CompleteMultipartUploadRequest.builder()
                    .bucket(S3Helper.BUCKET)
                    .key(formData.getKey())
                    .uploadId(formData.getUploadId())
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build();
            CompleteMultipartUploadResponse cResponse = s3Helper.getS3Client().completeMultipartUpload(request);

            return "{\"key\";\"" + cResponse.key() + "\",\"eTag\":\"" + cResponse.eTag() + "\"}";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s3Helper.getS3Client().close();
        }

        return "{\"message\":\"error: wtf!\"}";
    }

    @GetMapping(value = "/presign", produces = "application/json")
    @ResponseBody
    public String getPresignedURL(@RequestParam String mimeType) {
        if(mimeType == null) {
            throw new RuntimeException("ERROR: mimeType query parameter required!");
        }
//        return "help";
        PresignUrlInfo info = s3Helper.getUploadPresignedURL(mimeType);
//        System.out.println(url);
        return "{ \"url\" : \"" + info.getUrl() + "\",\"key\":\"" + info.getKey() + "\"}";
    }
}