package com.devops.backend.modules.user.service;

import com.devops.backend.common.exception.ApiException;
import com.devops.backend.modules.user.entity.Role;
import com.devops.backend.modules.user.repository.AdministratorRepository;
import com.devops.backend.modules.user.repository.GeneralUserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

    private final AdministratorRepository administratorRepository;
    private final GeneralUserRepository generalUserRepository;

    public UserRoleService(AdministratorRepository administratorRepository,
                           GeneralUserRepository generalUserRepository) {
        this.administratorRepository = administratorRepository;
        this.generalUserRepository = generalUserRepository;
    }

    public Role roleOf(String email) {
        if (administratorRepository.existsById(email)) return Role.ADMIN;
        if (generalUserRepository.existsById(email)) return Role.USER;
        throw ApiException.forbidden("ACCOUNT_ROLE_MISSING", "The account does not have an assigned role");
    }
}
