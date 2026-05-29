package com.handsofretail.hor.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequest {

    @NotNull
    private Long clientId;

    @NotBlank
    private String storeName;

    @NotBlank
    private String storeCode;

    private String address;

    private String contactNumber;
}