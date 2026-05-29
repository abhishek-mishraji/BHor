package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.auth.LoginRequest;
import com.handsofretail.hor.dto.response.auth.AuthResponse;
import com.handsofretail.hor.entity.AdminUser;
import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.exception.UnauthorizedException;
import com.handsofretail.hor.repository.AdminUserRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import com.handsofretail.hor.security.jwt.JwtService;
import com.handsofretail.hor.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final AdminUserRepository adminUserRepository;

        private final ClientUserRepository clientUserRepository;

        private final PasswordEncoder passwordEncoder;

        private final JwtService jwtService;

        @Override
        public AuthResponse login(LoginRequest request) {

                AdminUser adminUser = adminUserRepository
                                .findByEmail(request.getEmail())
                                .orElse(null);

                if (adminUser != null) {

                        boolean passwordMatches = passwordEncoder.matches(
                                        request.getPassword(),
                                        adminUser.getPasswordHash());

                        if (!passwordMatches) {
                                throw new UnauthorizedException("Invalid credentials");
                        }

                        String token = jwtService.generateToken(
                                        adminUser.getEmail(),
                                        adminUser.getRole().name());

                        return AuthResponse.builder()
                                        .token(token)
                                        .role(adminUser.getRole().name())
                                        .email(adminUser.getEmail())
                                        .fullName(adminUser.getFullName())
                                        .build();
                }

                ClientUser clientUser = clientUserRepository
                                .findByEmail(request.getEmail())
                                .orElse(null);

                if (clientUser != null) {

                        boolean passwordMatches = passwordEncoder.matches(
                                        request.getPassword(),
                                        clientUser.getPasswordHash());

                        if (!passwordMatches) {
                                throw new UnauthorizedException("Invalid credentials");
                        }

                        String token = jwtService.generateToken(
                                        clientUser.getEmail(),
                                        clientUser.getRole().name());

                        return AuthResponse.builder()
                                        .token(token)
                                        .role(clientUser.getRole().name())
                                        .email(clientUser.getEmail())
                                        .fullName(clientUser.getFullName())
                                        .build();
                }

                throw new UnauthorizedException("Invalid credentials");
        }
}