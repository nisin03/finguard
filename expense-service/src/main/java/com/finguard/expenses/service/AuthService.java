package com.finguard.expenses.service;

import org.springframework.stereotype.Service;

import com.finguard.expenses.dto.LoginRequest;
import com.finguard.expenses.exception.AuthLoginException;
import com.finguard.expenses.security.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {

    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";

    private final JwtUtil jwtUtil;

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String login(LoginRequest request) {
        if (!VALID_USERNAME.equals(request.username()) || !VALID_PASSWORD.equals(request.password())) {
            throw new AuthLoginException();
        }

        return jwtUtil.generateToken(request.username());
    }
}
