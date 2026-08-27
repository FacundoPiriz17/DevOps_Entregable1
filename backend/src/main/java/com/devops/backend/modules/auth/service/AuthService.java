package com.devops.backend.modules.auth.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.common.security.JwtService;
import com.devops.backend.modules.auth.dto.AuthResponse;
import com.devops.backend.modules.auth.dto.LoginRequest;
import com.devops.backend.modules.auth.dto.RegisterRequest;
import com.devops.backend.modules.auth.entity.Login;
import com.devops.backend.modules.auth.repository.LoginRepository;
import com.devops.backend.modules.user.entity.GeneralUser;
import com.devops.backend.modules.user.entity.Role;
import com.devops.backend.modules.user.entity.User;
import com.devops.backend.modules.user.repository.GeneralUserRepository;
import com.devops.backend.modules.user.repository.UserRepository;
import com.devops.backend.modules.user.service.UserRoleService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final LoginRepository loginRepository;
    private final GeneralUserRepository generalUserRepository;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       LoginRepository loginRepository,
                       GeneralUserRepository generalUserRepository,
                       UserRoleService userRoleService,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
        this.generalUserRepository = generalUserRepository;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsById(email)) {
            throw ApiException.conflict("EMAIL_ALREADY_USED", "Email is already in use");
        }

        User user = userRepository.save(new User(request.name().trim(), email, request.country().trim()));
        loginRepository.save(new Login(email, passwordEncoder.encode(request.password())));
        generalUserRepository.save(new GeneralUser(email));
        return toAuthResponse(user, Role.USER);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findById(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        Login login = loginRepository.findById(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), login.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!user.isActive()) {
            throw ApiException.forbidden("ACCOUNT_INACTIVE", "This account has been deactivated");
        }
        return toAuthResponse(user, userRoleService.roleOf(email));
    }

    private AuthResponse toAuthResponse(User user, Role role) {
        String token = jwtService.generateToken(user, role);
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getCountry(), role.name());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
