package com.handsofretail.hor.service;

import com.handsofretail.hor.dto.request.AnalyticsRequest;
import com.handsofretail.hor.dto.response.AnalyticsResponse;

public interface AnalyticsService {

    AnalyticsResponse getAnalytics(AnalyticsRequest request);
}
