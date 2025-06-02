package com.example.crabfood_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatsResponse {
    private long total;
    private long pending;
    private long processing;
    private long completed;
    private long cancelled;
    private double weeklyGrowthPercent;
}
