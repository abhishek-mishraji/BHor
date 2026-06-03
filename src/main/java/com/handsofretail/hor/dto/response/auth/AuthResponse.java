package com.handsofretail.hor.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Login / refresh response body.
 * Both access token and refresh token are delivered via HttpOnly cookies —
 * neither token is included in this payload.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String role;

    private String email;

    private String fullName;
}