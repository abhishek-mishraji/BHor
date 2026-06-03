package com.handsofretail.hor.service.auth;

import com.handsofretail.hor.dto.request.auth.LoginRequest;
import com.handsofretail.hor.dto.response.auth.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    /** Authenticates user, issues access token (body) + refresh token (HttpOnly cookie). */
    AuthResponse login(LoginRequest request, HttpServletResponse response);

    /**
     * Reads the refresh token from the HttpOnly cookie, validates it,
     * rotates it (new token in cookie), and returns a fresh access token.
     */
    AuthResponse refresh(HttpServletRequest request, HttpServletResponse response);

    /** Revokes the refresh token from cookie and clears the cookie. */
    void logout(HttpServletRequest request, HttpServletResponse response);
}