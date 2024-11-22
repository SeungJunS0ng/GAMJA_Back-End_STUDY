package com.study.board.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    private final S3Service s3Service;

    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("bucketName") String bucketName) {
        try {
            File tempFile = File.createTempFile("temp", file.getOriginalFilename());
            file.transferTo(tempFile);
            s3Service.uploadFile(bucketName, file.getOriginalFilename(), tempFile);
            return "File uploaded successfully!";
        } catch (Exception e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }
}

