package com.handsofretail.hor.dto.request;

import com.handsofretail.hor.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientStatusRequest {

    @NotNull(message = "Status is required")
    private Status status;
}
