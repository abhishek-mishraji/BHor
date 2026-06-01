package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MonthlyReportUploadResponse {

    private int totalRows;

    private int insertedRows;

    private long deletedRows;
}