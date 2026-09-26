package com.example.demo.service;

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

    public List<SocialAccount> getUserAccounts(User user) {
        return socialAccountRepository.findByUser(user);
    }

    public boolean isPlatformConnected(
            User user,
            SocialPlatform platform
    ) {
        return socialAccountRepository
                .existsByUserAndPlatform(user, platform);
    }
}