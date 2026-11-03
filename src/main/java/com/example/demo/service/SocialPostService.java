package com.example.demo.service;

import com.example.demo.entity.SocialPost;
import com.example.demo.entity.SocialPostStatus;
import com.example.demo.repository.SocialPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SocialPostService {

    private final SocialPostRepository socialPostRepository;

    public SocialPostService(SocialPostRepository socialPostRepository) {
        this.socialPostRepository = socialPostRepository;
    }

    // Create a new draft
    public SocialPost createDraft(String userEmail, String content) {

        SocialPost post = SocialPost.builder()
                .userEmail(userEmail)
                .content(content)
                .status(SocialPostStatus.DRAFT)
                .build();

        return socialPostRepository.save(post);
    }

    // Get all drafts belonging to a user
    public List<SocialPost> getDrafts(String userEmail) {

        return socialPostRepository.findByUserEmailAndStatus(
                userEmail,
                SocialPostStatus.DRAFT
        );
    }

    // Get all posts belonging to a user
    public List<SocialPost> getAllPosts(String userEmail) {

        return socialPostRepository.findByUserEmail(userEmail);
    }

    // Get a specific post belonging to a user
    public SocialPost getPost(Long postId, String userEmail) {

        return socialPostRepository.findByIdAndUserEmail(
                postId,
                userEmail
        ).orElseThrow(() ->
                new RuntimeException("Post not found")
        );
    }
}