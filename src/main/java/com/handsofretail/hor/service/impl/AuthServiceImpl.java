package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.auth.LoginRequest;
import com.handsofretail.hor.dto.response.auth.AuthResponse;
import com.handsofretail.hor.entity.AdminUser;
import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.entity.RefreshToken;
import com.handsofretail.hor.exception.UnauthorizedException;
import com.handsofretail.hor.repository.AdminUserRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import com.handsofretail.hor.service.auth.AuthService;
import com.handsofretail.hor.service.auth.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminUserRepository adminUserRepository;
    private final ClientUserRepository clientUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // ─────────────────────────────────────────────────────────────
    // Login
    // ─────────────────────────────────────────────────────────────

    @Override
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {

        // Try admin first
        AdminUser adminUser = adminUserRepository.findByEmail(request.getEmail()).orElse(null);
        if (adminUser != null) {
            verifyPassword(request.getPassword(), adminUser.getPasswordHash());
            issueTokenCookies(adminUser.getEmail(), adminUser.getRole().name(), response);
            return AuthResponse.builder()
                    .role(adminUser.getRole().name())
                    .email(adminUser.getEmail())
                    .fullName(adminUser.getFullName())
                    .build();
        }

        // Try client
        ClientUser clientUser = clientUserRepository.findByEmail(request.getEmail()).orElse(null);
        if (clientUser != null) {
            verifyPassword(request.getPassword(), clientUser.getPasswordHash());
            issueTokenCookies(clientUser.getEmail(), clientUser.getRole().name(), response);
            return AuthResponse.builder()
                    .role(clientUser.getRole().name())
                    .email(clientUser.getEmail())
                    .fullName(clientUser.getFullName())
                    .build();
        }

        throw new UnauthorizedException("Invalid email or password");
    }

    // ─────────────────────────────────────────────────────────────
    // Refresh
    // ─────────────────────────────────────────────────────────────

    @Override
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        // Validate refresh cookie; throws 401 if missing/expired/revoked
        RefreshToken stored = tokenService.validateRefreshTokenFromCookie(request);

        // Rotate: revoke old refresh token, issue new one in cookie
        tokenService.rotateRefreshToken(stored, response);

        // Issue fresh access token in cookie
        tokenService.setAccessTokenCookie(stored.getUserEmail(), stored.getUserRole(), response);

        return AuthResponse.builder()
                .role(stored.getUserRole())
                .email(stored.getUserEmail())
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // Logout
    // ─────────────────────────────────────────────────────────────

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        RefreshToken stored = tokenService.validateRefreshTokenFromCookie(request);
        tokenService.revokeAllAndClearCookies(stored.getUserEmail(), response);
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    /** Sets both the access token (Path=/) and refresh token (Path=/api/v1/auth) as HttpOnly cookies. */
    private void issueTokenCookies(String email, String role, HttpServletResponse response) {
        tokenService.setAccessTokenCookie(email, role, response);
        tokenService.createAndSetRefreshToken(email, role, response);
    }

    private void verifyPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }
}