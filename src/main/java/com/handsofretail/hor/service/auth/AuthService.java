package com.handsofretail.hor.service.auth;

import com.handsofretail.hor.dto.request.auth.LoginRequest;
import com.handsofretail.hor.dto.response.auth.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);
}