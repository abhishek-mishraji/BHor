package com.handsofretail.hor.dto.request;

import com.handsofretail.hor.enums.StoreRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request body for adding a member to a store")
public class StoreMemberRequest {

    @NotNull(message = "Store ID is required")
    @Schema(description = "ID of the store", example = "1")
    private Long storeId;

    @NotNull(message = "Client ID is required")
    @Schema(description = "ID of the client to add", example = "3")
    private Long clientId;

    @NotNull(message = "Role is required (OWNER or PARTNER)")
    @Schema(description = "Role of the client in the store", example = "PARTNER", allowableValues = {"OWNER", "PARTNER"})
    private StoreRole role;
}
