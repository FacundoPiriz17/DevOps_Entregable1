package com.devops.backend.modules.auth.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.common.security.JwtService;
import com.devops.backend.modules.auth.dto.AuthResponse;
import com.devops.backend.modules.auth.dto.LoginRequest;
import com.devops.backend.modules.auth.dto.RegisterRequest;
import com.devops.backend.modules.user.entity.Role;
import com.devops.backend.modules.user.entity.User;
import com.devops.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_createsUserAndReturnsToken() {
        RegisterRequest request = new RegisterRequest("Julia Fernandez", "julia@example.com", "ContraseniaSegura123");
        when(userRepository.existsByEmail("julia@example.com")).thenReturn(false);
        when(passwordEncoder.encode("ContraseniaSegura123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("token-123");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("token-123");
        assertThat(response.email()).isEqualTo("julia@example.com");
        assertThat(response.role()).isEqualTo("USER");
    }

    @Test
    void register_emailAlreadyUsed_throwsConflict() {
        RegisterRequest request = new RegisterRequest("Julia Fernandez", "julia@example.com", "ContraseniaSegura123");
        when(userRepository.existsByEmail("julia@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("already in use");

        verify(userRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void login_validCredentials_returnsToken() {
        User user = new User("Julia Fernandez", "julia@example.com", "hashed", Role.USER);
        LoginRequest request = new LoginRequest("julia@example.com", "ContraseniaSegura123");
        when(userRepository.findByEmail("julia@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("ContraseniaSegura123", "hashed")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("token-123");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("token-123");
    }

    @Test
    void login_wrongPassword_throwsBadCredentials() {
        User user = new User("Julia Fernandez", "julia@example.com", "hashed", Role.USER);
        LoginRequest request = new LoginRequest("julia@example.com", "wrong-password");
        when(userRepository.findByEmail("julia@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request)).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_unknownEmail_throwsBadCredentials() {
        LoginRequest request = new LoginRequest("unknown@example.com", "whatever123");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request)).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_inactiveAccount_throwsForbidden() {
        User user = new User("Julia Fernandez", "julia@example.com", "hashed", Role.USER);
        user.setActive(false);
        LoginRequest request = new LoginRequest("julia@example.com", "ContraseniaSegura123");
        when(userRepository.findByEmail("julia@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("ContraseniaSegura123", "hashed")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("deactivated");
    }
}
