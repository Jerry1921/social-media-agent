package com.example.demo.controller;

import com.example.demo.entity.SocialAccount;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SocialAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social-accounts")
public class SocialAccountController {

    private final SocialAccountService socialAccountService;
    private final UserRepository userRepository;

    public SocialAccountController(
            SocialAccountService socialAccountService,
            UserRepository userRepository
    ) {
        this.socialAccountService = socialAccountService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<SocialAccount>> getMyAccounts(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        List<SocialAccount> accounts =
                socialAccountService.getUserAccounts(user);

        return ResponseEntity.ok(accounts);
    }
}