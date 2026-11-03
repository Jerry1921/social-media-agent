package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path uploadDirectory =
            Path.of("uploads").toAbsolutePath().normalize();

    public String saveImage(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }

        String contentType = image.getContentType();

        String extension;

        if ("image/jpeg".equals(contentType)) {
            extension = ".jpg";
        } else if ("image/png".equals(contentType)) {
            extension = ".png";
        } else if ("image/webp".equals(contentType)) {
            extension = ".webp";
        } else {
            throw new IllegalArgumentException(
                    "Only JPG, PNG and WEBP images are allowed"
            );
        }

        try {
            Files.createDirectories(uploadDirectory);

            String fileName = UUID.randomUUID() + extension;

            Path targetPath = uploadDirectory.resolve(fileName);

            Files.copy(image.getInputStream(), targetPath);

            return targetPath.toString();

        } catch (IOException e) {
            throw new RuntimeException("Could not save image", e);
        }
    }
}