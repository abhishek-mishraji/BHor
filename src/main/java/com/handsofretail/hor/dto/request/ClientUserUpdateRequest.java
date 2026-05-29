package com.handsofretail.hor.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientUserUpdateRequest {

    private String fullName;

    @Email
    private String email;

    /**
     * Optional new password. If provided, will be encoded and stored.
     */
    private String password;

    private String phoneNumber;

    private String address;
}