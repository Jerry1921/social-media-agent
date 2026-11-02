package com.example.demo.controller;

import com.example.demo.dto.LinkedInPostRequest;
import com.example.demo.service.LinkedInPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/linkedin")
public class LinkedInPostController {

    private final LinkedInPostService linkedInPostService;

    public LinkedInPostController(
            LinkedInPostService linkedInPostService
    ) {
        this.linkedInPostService = linkedInPostService;
    }

    @PostMapping("/posts")
    public ResponseEntity<String> createPost(
            Authentication authentication,
            @RequestBody LinkedInPostRequest request
    ) {

        String userEmail = authentication.getName();
        System.out.println("Authenticated user: " + userEmail);

        String result =
                linkedInPostService.createPost(
                        userEmail,
                        request.getContent()
                );

        return ResponseEntity.ok(result);
    }

    @PostMapping(
            value = "/posts/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> createImagePost(
            Authentication authentication,
            @RequestParam("content") String content,
            @RequestParam("image") MultipartFile image
    ) {
        String userEmail = authentication.getName();

        String result = linkedInPostService.createImagePost(
                userEmail,
                content,
                image
        );

        return ResponseEntity.ok(result);
    }
}