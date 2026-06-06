package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StoreMemberResponse {

    private Long clientId;

    private String clientName;

    private String role;
}
