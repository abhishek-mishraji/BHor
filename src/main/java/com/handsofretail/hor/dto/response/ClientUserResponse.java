package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientUserResponse {

    private Long clientId;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String address;

    private String status;

    private String role;
}