package com.handsofretail.hor.service.validation;

import com.handsofretail.hor.exception.DuplicateResourceException;
import com.handsofretail.hor.repository.AdminUserRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEmailValidationService {

    private final AdminUserRepository adminUserRepository;
    private final ClientUserRepository clientUserRepository;

    public void validateUniqueEmail(String email) {
        boolean exists = adminUserRepository.existsByEmail(email)
                || clientUserRepository.existsByEmail(email);

        if (exists) {
            throw new DuplicateResourceException("Email already exists");
        }
    }
}
