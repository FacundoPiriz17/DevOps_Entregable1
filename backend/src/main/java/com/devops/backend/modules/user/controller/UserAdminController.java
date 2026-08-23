package com.devops.backend.modules.user.controller;

import com.devops.backend.modules.user.dto.GameUsageItem;
import com.devops.backend.modules.user.dto.UserBasicResponse;
import com.devops.backend.modules.user.service.UserAdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping("/{userId}")
    public UserBasicResponse getUser(@PathVariable Long userId) {
        return userAdminService.getUser(userId);
    }

    @GetMapping("/{userId}/usage")
    public List<GameUsageItem> getUsage(@PathVariable Long userId) {
        return userAdminService.getUsage(userId);
    }

    @PatchMapping("/{userId}/deactivate")
    public UserBasicResponse deactivate(@PathVariable Long userId) {
        return userAdminService.deactivate(userId);
    }
}
