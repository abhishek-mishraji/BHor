package com.handsofretail.hor.service.auth;

import com.handsofretail.hor.entity.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenService {

    // ── Access Token Cookie ────────────────────────────────────────────────

    /** Generates a new access token (JWT, 15 min) and sets it as an HttpOnly cookie (Path=/). */
    void setAccessTokenCookie(String userEmail, String userRole, HttpServletResponse response);

    /** Clears the access token cookie (MaxAge=0). */
    void clearAccessTokenCookie(HttpServletResponse response);

    // ── Refresh Token Cookie ───────────────────────────────────────────────

    /**
     * Generates an opaque refresh token, persists its SHA-256 hash to the DB,
     * and sets it as an HttpOnly cookie (Path=/api/v1/auth, 7 days).
     * Returns the raw token (used internally for rotation).
     */
    String createAndSetRefreshToken(String userEmail, String userRole, HttpServletResponse response);

    /**
     * Reads the refresh cookie, validates it against the DB
     * (not expired, not revoked). Throws UnauthorizedException if invalid.
     */
    RefreshToken validateRefreshTokenFromCookie(HttpServletRequest request);

    /**
     * Revokes the old refresh token, creates a new one, and sets the new cookie.
     */
    String rotateRefreshToken(RefreshToken old, HttpServletResponse response);

    /**
     * Revokes all active refresh tokens for the user and clears BOTH cookies.
     */
    void revokeAllAndClearCookies(String userEmail, HttpServletResponse response);
}
