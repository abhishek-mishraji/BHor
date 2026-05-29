package com.handsofretail.hor.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handsofretail.hor.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationFailureHandler
        implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> body = ApiResponse.error("Authentication failed");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}