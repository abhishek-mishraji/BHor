package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.ClientUserResponse;
import com.handsofretail.hor.entity.ClientUser;

public final class ClientUserMapper {

    private ClientUserMapper() {
    }

    public static ClientUserResponse toResponse(ClientUser client) {
        return ClientUserResponse.builder()
                .clientId(client.getClientId())
                .fullName(client.getFullName())
                .email(client.getEmail())
                .phoneNumber(client.getPhoneNumber())
                .address(client.getAddress())
                .status(client.getStatus().name())
                .role(client.getRole().name())
                .build();
    }
}