package com.devops.backend.modules.auth.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.common.security.JwtService;
import com.devops.backend.modules.auth.dto.AuthResponse;
import com.devops.backend.modules.auth.dto.LoginRequest;
import com.devops.backend.modules.auth.dto.RegisterRequest;
import com.devops.backend.modules.user.entity.Role;
import com.devops.backend.modules.user.entity.User;
import com.devops.backend.modules.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw ApiException.conflict("EMAIL_ALREADY_USED", "Email is already in use");
        }
        User user = new User(request.name(), request.email(), passwordEncoder.encode(request.password()), Role.USER);
        user = userRepository.save(user);
        return toAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!user.isActive()) {
            throw ApiException.forbidden("ACCOUNT_INACTIVE", "This account has been deactivated");
        }
        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
