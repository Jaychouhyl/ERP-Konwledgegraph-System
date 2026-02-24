package com.smartx.auth.application;

import org.springframework.stereotype.Service;

@Service
public class TokenService {
    public String generateToken(String userId) {
        return "mock-token-" + userId;
    }
}
