package com.example.demo.controller;

import com.example.demo.dto.LinkedInPostRequest;
import com.example.demo.service.LinkedInPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}