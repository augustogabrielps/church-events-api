package com.serve.service;

import com.serve.domain.User;
import com.serve.dto.LoginRequest;
import com.serve.dto.LoginResponse;
import com.serve.repository.UserRepository;
import com.serve.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new LoginResponse(jwtService.generateToken(user));
    }
}
