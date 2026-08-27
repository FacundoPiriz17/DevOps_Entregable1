package com.devops.backend.modules.user.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.user.dto.UserBasicResponse;
import com.devops.backend.modules.user.entity.User;
import com.devops.backend.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleService userRoleService;

    public UserService(UserRepository userRepository, UserRoleService userRoleService) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
    }

    @Transactional(readOnly = true)
    public UserBasicResponse getCurrentUser(String email) {
        User user = userRepository.findById(email)
                .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "User does not exist"));
        return UserBasicResponse.from(user, userRoleService.roleOf(email));
    }
}
