package com.handsofretail.hor.dto.request;

import com.handsofretail.hor.enums.StoreRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreMemberRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private StoreRole role;
}
