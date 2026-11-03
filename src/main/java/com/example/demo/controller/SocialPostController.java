package com.example.demo.controller;

import com.example.demo.entity.SocialPost;
import com.example.demo.service.SocialPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.SocialPostRequest;

import java.util.List;

@RestController
@RequestMapping("/api/social-posts")
public class SocialPostController {

    private final SocialPostService socialPostService;

    public SocialPostController(SocialPostService socialPostService) {
        this.socialPostService = socialPostService;
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
}