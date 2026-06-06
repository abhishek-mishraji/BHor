package com.handsofretail.hor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "Store member details")
public class StoreMemberResponse {

    @Schema(description = "ID of the store", example = "1")
    private Long storeId;

    @Schema(description = "ID of the client", example = "3")
    private Long clientId;

    @Schema(description = "Full name of the client", example = "John Smith")
    private String clientName;

    @Schema(description = "Role of the client in the store", example = "PARTNER")
    private String role;
}
