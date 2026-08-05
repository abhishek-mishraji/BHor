package com.handsofretail.hor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatasetDto {

    private String label;

    private String metric;

    private List<Object> data;

    private BigDecimal valueA;

    private BigDecimal valueB;

    private BigDecimal difference;

    private BigDecimal percentageDifference;
}
