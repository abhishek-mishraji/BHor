package com.handsofretail.hor.service.validation;

import com.handsofretail.hor.exception.DuplicateResourceException;
import com.handsofretail.hor.repository.AdminUserRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserEmailValidationServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private ClientUserRepository clientUserRepository;

    @InjectMocks
    private UserEmailValidationService userEmailValidationService;

    @Test
    void shouldThrowWhenEmailExistsInAdminUsers() {
        when(adminUserRepository.existsByEmail("shared@company.com")).thenReturn(true);

        assertThatThrownBy(() -> userEmailValidationService.validateUniqueEmail("shared@company.com"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists");
    }

    @Test
    void shouldThrowWhenEmailExistsInClientUsers() {
        when(adminUserRepository.existsByEmail("shared@company.com")).thenReturn(false);
        when(clientUserRepository.existsByEmail("shared@company.com")).thenReturn(true);

        assertThatThrownBy(() -> userEmailValidationService.validateUniqueEmail("shared@company.com"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists");
    }

    @Test
    void shouldPassWhenEmailDoesNotExistInAnyUserTable() {
        when(adminUserRepository.existsByEmail("unique@company.com")).thenReturn(false);
        when(clientUserRepository.existsByEmail("unique@company.com")).thenReturn(false);

        assertThatNoException()
                .isThrownBy(() -> userEmailValidationService.validateUniqueEmail("unique@company.com"));
    }
}
