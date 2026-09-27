package com.example.demo.service;

import com.example.demo.dto.SocialAccountResponse;
import com.example.demo.entity.SocialAccount;
import com.example.demo.entity.SocialPlatform;
import com.example.demo.entity.User;
import com.example.demo.repository.SocialAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialAccountService {

    private final SocialAccountRepository socialAccountRepository;

    public SocialAccountService(
            SocialAccountRepository socialAccountRepository
    ) {
        this.socialAccountRepository = socialAccountRepository;
    }

    public List<SocialAccountResponse> getUserAccounts(User user) {

        return socialAccountRepository.findByUser(user)
                .stream()
                .map(account -> SocialAccountResponse.builder()
                        .id(account.getId())
                        .platform(account.getPlatform())
                        .platformUserId(account.getPlatformUserId())
                        .connectedAt(account.getConnectedAt())
                        .build())
                .toList();
    }

    public boolean isPlatformConnected(
            User user,
            SocialPlatform platform
    ) {
        return socialAccountRepository
                .existsByUserAndPlatform(user, platform);
    }
}