package com.handsofretail.hor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreResponse {

    private Long storeId;

    private Long clientId;

    private String clientName;

    private String storeName;

    private String storeCode;

    private String address;

    private String contactNumber;

    private String status;

    /** Populated in CLIENT-context responses only. Values: "OWNER" | "PARTNER". Null in admin responses. */
    private String clientRole;
}
