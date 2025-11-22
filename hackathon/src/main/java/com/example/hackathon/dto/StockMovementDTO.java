package com.example.hackathon.dto;

import lombok.Data;

@Data
public class StockMovementDTO {
    private Long productId;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private Long supplierId;
    private Integer quantity;
    private String type; // RECEIPT, DELIVERY, TRANSFER, ADJUSTMENT
    private String status; // PENDING, DONE
}
