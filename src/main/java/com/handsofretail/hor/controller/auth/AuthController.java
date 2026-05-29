package com.handsofretail.hor.controller.auth;

import com.handsofretail.hor.dto.request.auth.LoginRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.auth.AuthResponse;
import com.handsofretail.hor.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response));
    }
}