package com.example.hackathon.dto;

import lombok.Data;

@Data
public class DashboardStatsDTO {
    private long totalProducts;
    private long lowStockItems;
    private long pendingReceipts;
    private long pendingDeliveries;
}
