package com.handsofretail.hor.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthResponse {

    private String token;

    private String role;

    private String email;

    private String fullName;
}