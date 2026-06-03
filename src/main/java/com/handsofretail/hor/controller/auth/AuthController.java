package com.handsofretail.hor.controller.auth;

import com.handsofretail.hor.dto.request.auth.LoginRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.auth.AuthResponse;
import com.handsofretail.hor.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/login
     * Returns accessToken in body + sets HttpOnly refresh_token cookie.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = authService.login(request, response);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    /**
     * POST /api/v1/auth/refresh
     * Reads refresh_token cookie, rotates it, returns new accessToken in body.
     * No request body required.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = authService.refresh(request, response);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", authResponse));
    }

    /**
     * POST /api/v1/auth/logout
     * Revokes the refresh token from the DB and clears the cookie.
     * No request body required.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        authService.logout(request, response);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}