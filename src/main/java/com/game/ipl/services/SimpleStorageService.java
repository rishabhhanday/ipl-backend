package com.game.ipl.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.game.ipl.exceptions.FailedCreateUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@Service
public class SimpleStorageService {
    private static final String BUCKET_NAME = "ipl-participants-image";
    private static final String BUCKET_PUBLIC_URL = "https://ipl-participants-image.s3.ap-south-1.amazonaws.com/";
    @Autowired
    private AmazonS3 s3;

    public String uploadUserImage(String username, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new FailedCreateUserException("Cannot upload empty file");
        }
        //Check if the file is an image
        if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(file.getContentType())) {
            throw new FailedCreateUserException("FIle uploaded is not an image , PNG and JPEG type supported");
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String key = username + "_" + file.getOriginalFilename();
        s3.putObject(BUCKET_NAME, username + "_" + file.getOriginalFilename(), file.getInputStream(), objectMetadata);

        return BUCKET_PUBLIC_URL + key;
    }
}
