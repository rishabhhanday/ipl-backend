package com.game.ipl.services;

import com.game.ipl.exceptions.FailedCreateUserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@Service
public class FileStorageService {
    @Value("${ipl.participants.image.location}")
    private String fileStorageBaseDestination;

    public String saveUserImage(String username, MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            throw new FailedCreateUserException("Cannot upload empty file");
        }
        //Check if the file is an image
        if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(multipartFile.getContentType())) {
            throw new FailedCreateUserException("FIle uploaded is not an image , PNG and JPEG type supported");
        }

        String destination = fileStorageBaseDestination + username + "_" + multipartFile.getOriginalFilename();
        File file = new File(destination);
        multipartFile.transferTo(file);
        return destination;
    }
}
