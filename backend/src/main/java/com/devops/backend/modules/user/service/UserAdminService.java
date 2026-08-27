package com.devops.backend.modules.user.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.user.dto.UserBasicResponse;
import com.devops.backend.modules.user.entity.User;
import com.devops.backend.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAdminService {

    private final UserRepository userRepository;

    public UserAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserBasicResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(UserBasicResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserBasicResponse getUser(Long userId) {
        return UserBasicResponse.from(findUserOrThrow(userId));
    }

    @Transactional
    public UserBasicResponse deactivate(Long userId) {
        User user = findUserOrThrow(userId);
        user.setActive(false);
        return UserBasicResponse.from(user);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "User does not exist"));
    }
}