package com.handsofretail.hor.dto.request;

import com.handsofretail.hor.enums.AggregateType;
import com.handsofretail.hor.enums.GroupByField;
import com.handsofretail.hor.enums.ReportType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class AnalyticsRequest {

    @NotNull(message = "reportType is required (DAILY or MONTHLY)")
    private ReportType reportType;

    @NotNull(message = "groupBy is required (DATE, MONTH, YEAR, STORE, DEPARTMENT)")
    private GroupByField groupBy;

    @NotEmpty(message = "At least one metric is required")
    private List<String> metric;

    private AggregateType aggregate = AggregateType.SUM;

    // Admin-only scope filters (ignored on client endpoint)
    private List<Long> storeIds;
    private Long clientId;

    // Daily filters
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;

    // Monthly filters
    private Integer month;
    private List<Integer> year;
    private String departmentId;
}
