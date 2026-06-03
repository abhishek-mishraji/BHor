package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.entity.RefreshToken;
import com.handsofretail.hor.exception.UnauthorizedException;
import com.handsofretail.hor.repository.RefreshTokenRepository;
import com.handsofretail.hor.security.jwt.JwtService;
import com.handsofretail.hor.service.auth.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    // ── Access Token Cookie Config ──────────────────────────────────
    @Value("${jwt.access-cookie.name:access_token}")
    private String accessCookieName;

    // ── Refresh Token Cookie Config ─────────────────────────────────
    @Value("${jwt.refresh-cookie.name:refresh_token}")
    private String refreshCookieName;

    /** false in local HTTP dev; true in production (HTTPS required). */
    @Value("${jwt.refresh-cookie.secure:false}")
    private boolean cookieSecure;

    // ─────────────────────────────────────────────────────────────
    // Access Token Cookie
    // ─────────────────────────────────────────────────────────────

    @Override
    public void setAccessTokenCookie(String userEmail, String userRole, HttpServletResponse response) {
        String jwt = jwtService.generateAccessToken(userEmail, userRole);
        int maxAge = (int) (jwtService.getAccessTokenExpirationMs() / 1000);
        addCookie(response, accessCookieName, jwt, "/", maxAge);
    }

    @Override
    public void clearAccessTokenCookie(HttpServletResponse response) {
        addCookie(response, accessCookieName, "", "/", 0);
    }

    // ─────────────────────────────────────────────────────────────
    // Refresh Token Cookie
    // ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public String createAndSetRefreshToken(String userEmail, String userRole, HttpServletResponse response) {
        String rawToken = jwtService.generateRefreshToken();
        String tokenHash = jwtService.hashToken(rawToken);
        long expirationMs = jwtService.getRefreshTokenExpirationMs();

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(tokenHash)
                .userEmail(userEmail)
                .userRole(userRole)
                .expiresAt(LocalDateTime.now().plusSeconds(expirationMs / 1000))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(entity);
        // Path=/api/v1/auth — browser only sends this cookie to auth endpoints
        addCookie(response, refreshCookieName, rawToken, "/api/v1/auth", (int) (expirationMs / 1000));

        return rawToken;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshTokenFromCookie(HttpServletRequest request) {
        String rawToken = extractCookieValue(request, refreshCookieName);
        if (rawToken == null) {
            throw new UnauthorizedException("Refresh token cookie is missing");
        }

        String hash = jwtService.hashToken(rawToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (token.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        return token;
    }

    @Override
    @Transactional
    public String rotateRefreshToken(RefreshToken old, HttpServletResponse response) {
        // Revoke old token
        old.setRevoked(true);
        refreshTokenRepository.save(old);
        // Issue a new refresh token cookie
        return createAndSetRefreshToken(old.getUserEmail(), old.getUserRole(), response);
    }

    @Override
    @Transactional
    public void revokeAllAndClearCookies(String userEmail, HttpServletResponse response) {
        refreshTokenRepository.revokeAllByUserEmail(userEmail);
        clearAccessTokenCookie(response);
        addCookie(response, refreshCookieName, "", "/api/v1/auth", 0);
    }

    // ─────────────────────────────────────────────────────────────
    // Cookie helpers
    // ─────────────────────────────────────────────────────────────

    private void addCookie(HttpServletResponse response, String name, String value, String path, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath(path);
        cookie.setMaxAge(maxAgeSeconds);
        response.addCookie(cookie);
    }

    private String extractCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
