package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StoreResponse {

    private Long storeId;

    private Long clientId;

    private String clientName;

    private String storeName;

    private String storeCode;

    private String address;

    private String contactNumber;

    private String status;
}