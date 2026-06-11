package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class AnalyticsResponse {

    private List<String> labels;

    private List<DatasetDto> datasets;

    private Map<String, Object> meta;
}
