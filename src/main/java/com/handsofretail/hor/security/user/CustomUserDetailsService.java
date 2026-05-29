package com.handsofretail.hor.security.user;

import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.exception.UnauthorizedException;
import com.handsofretail.hor.repository.ClientUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final ClientUserRepository clientUserRepository;

    public ClientUser getCurrentClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        String email = authentication.getName();
        return clientUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }
}