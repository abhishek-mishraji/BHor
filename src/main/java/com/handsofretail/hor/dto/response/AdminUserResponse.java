package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminUserResponse {

    private Long adminId;
    private String fullName;
    private String email;
    private String role;
}