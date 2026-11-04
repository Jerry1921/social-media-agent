package com.example.demo.controller;

import com.example.demo.entity.SocialPost;
import com.example.demo.service.ImageStorageService;
import com.example.demo.service.SocialPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.SocialPostRequest;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/social-posts")
public class SocialPostController {

    private final SocialPostService socialPostService;

    private final ImageStorageService imageStorageService;

    public SocialPostController(SocialPostService socialPostService,
                                ImageStorageService imageStorageService) {
        this.socialPostService = socialPostService;
        this.imageStorageService = imageStorageService;
    }

    // Create a draft
    @PostMapping("/drafts")
    public ResponseEntity<SocialPost> createDraft(
            Authentication authentication,
            @RequestBody SocialPostRequest request
    ) {
        String userEmail = authentication.getName();

        SocialPost post = socialPostService.createDraft(
                userEmail,
                request.getContent()
        );

        return ResponseEntity.ok(post);
    }

    // Get all drafts
    @GetMapping("/drafts")
    public ResponseEntity<List<SocialPost>> getDrafts(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                socialPostService.getDrafts(userEmail)
        );
    }

    // Get all posts
    @GetMapping
    public ResponseEntity<List<SocialPost>> getAllPosts(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                socialPostService.getAllPosts(userEmail)
        );
    }

    // Get one post
    @GetMapping("/{postId}")
    public ResponseEntity<SocialPost> getPost(
            Authentication authentication,
            @PathVariable Long postId
    ) {
        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                socialPostService.getPost(postId, userEmail)
        );
    }

    @PostMapping(
            value = "/drafts/with-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<SocialPost> createDraftWithImage(
            Authentication authentication,
            @RequestParam("content") String content,
            @RequestParam("image") MultipartFile image
    ) {

        String userEmail = authentication.getName();

        String imagePath = imageStorageService.saveImage(image);

        SocialPost post = socialPostService.createDraft(
                userEmail,
                content,
                imagePath
        );

        return ResponseEntity.ok(post);
    }
}