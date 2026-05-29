package com.handsofretail.hor.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreUpdateRequest {

    // Optional: move store to another client
    private Long clientId;

    private String storeName;

    private String storeCode;

    private String address;

    private String contactNumber;
}