package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialPlatform platform;

    @Column(nullable = false)
    private String platformUserId;

    @Column(nullable = false)
    private String accessToken;

    private String refreshToken;

    private LocalDateTime tokenExpiresAt;

    private LocalDateTime connectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        connectedAt = LocalDateTime.now();
    }
}