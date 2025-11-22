package com.example.hackathon.models;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Types: "RECEIPT", "DELIVERY", "TRANSFER", "ADJUSTMENT"
    private String type;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Supplier supplier; // Nullable, for Receipts


    @ManyToOne
    private Warehouse fromWarehouse; // Null for Receipts

    @ManyToOne
    private Warehouse toWarehouse;   // Null for Deliveries

    private Integer quantity;

    private LocalDateTime timestamp;
    private String status; // "DONE", "PENDING"

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
        if(this.status == null) this.status = "DONE";
    }
}
