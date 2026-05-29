package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.AdminUserResponse;
import com.handsofretail.hor.entity.AdminUser;

import lombok.Builder;

@Builder
public final class AdminUserMapper {

    private AdminUserMapper() {
    }

    public static AdminUserResponse toResponse(AdminUser adminUser) {
        return AdminUserResponse.builder()
                .adminId(adminUser.getAdminId())
                .fullName(adminUser.getFullName())
                .email(adminUser.getEmail())
                .role(adminUser.getRole().name())
                .build();
    }
}