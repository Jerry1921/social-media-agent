package com.example.demo.controller;

import com.example.demo.service.LinkedInOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/oauth/linkedin")
public class LinkedInOAuthController {

    private final LinkedInOAuthService linkedInOAuthService;

    public LinkedInOAuthController(
            LinkedInOAuthService linkedInOAuthService
    ) {
        this.linkedInOAuthService = linkedInOAuthService;
    }

    @GetMapping("/authorize")
    public ResponseEntity<String> authorize() {

        String state = UUID.randomUUID().toString();

        String authorizationUrl =
                linkedInOAuthService.buildAuthorizationUrl(state);

        return ResponseEntity.ok(authorizationUrl);
    }
}