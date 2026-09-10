package com.example.demo.dto;

import com.example.demo.entity.AuthenticationProvider;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;

    private String fullName;

    private String email;

    private AuthenticationProvider provider;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
