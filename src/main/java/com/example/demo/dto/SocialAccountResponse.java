package com.example.demo.dto;

import com.example.demo.entity.SocialPlatform;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SocialAccountResponse {

    private Long id;
    private SocialPlatform platform;
    private String platformUserId;
    private LocalDateTime connectedAt;
}