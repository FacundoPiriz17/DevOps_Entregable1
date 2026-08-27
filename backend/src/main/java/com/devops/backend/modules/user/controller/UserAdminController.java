package com.devops.backend.modules.user.controller;

import com.devops.backend.common.config.OpenApiConfig;
import com.devops.backend.modules.user.dto.UserBasicResponse;
import com.devops.backend.modules.user.service.UserAdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "User administration", description = "Consulta y baja lÃ³gica de usuarios")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public List<UserBasicResponse> getUsers() {
        return userAdminService.getUsers();
    }

    @GetMapping("/{email}")
    public UserBasicResponse getUser(@PathVariable String email) {
        return userAdminService.getUser(email);
    }

    @PatchMapping("/{email}/deactivate")
    public UserBasicResponse deactivate(@PathVariable String email) {
        return userAdminService.deactivate(email);
    }
}
