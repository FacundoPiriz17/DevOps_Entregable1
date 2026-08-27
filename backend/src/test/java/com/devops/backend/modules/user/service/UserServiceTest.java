package com.devops.backend.modules.user.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.user.dto.UserBasicResponse;
import com.devops.backend.modules.user.entity.Role;
import com.devops.backend.modules.user.entity.User;
import com.devops.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getCurrentUser_existingUser_returnsBasicInfo() {
        User user = new User("Julia Fernandez", "julia@example.com", "hashed", Role.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserBasicResponse response = userService.getCurrentUser(1L);

        assertThat(response.name()).isEqualTo("Julia Fernandez");
        assertThat(response.email()).isEqualTo("julia@example.com");
    }

    @Test
    void getCurrentUser_nonExistingUser_throwsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser(1L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("does not exist");
    }
}