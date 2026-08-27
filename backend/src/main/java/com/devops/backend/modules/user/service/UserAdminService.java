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
    private final UserRoleService userRoleService;

    public UserAdminService(UserRepository userRepository, UserRoleService userRoleService) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
    }

    @Transactional(readOnly = true)
    public List<UserBasicResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserBasicResponse.from(user, userRoleService.roleOf(user.getEmail())))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserBasicResponse getUser(String email) {
        User user = findUserOrThrow(email);
        return UserBasicResponse.from(user, userRoleService.roleOf(email));
    }

    @Transactional
    public UserBasicResponse deactivate(String email) {
        User user = findUserOrThrow(email);
        user.setActive(false);
        return UserBasicResponse.from(user, userRoleService.roleOf(email));
    }

    private User findUserOrThrow(String email) {
        return userRepository.findById(email)
                .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "User does not exist"));
    }
}
