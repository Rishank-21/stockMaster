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
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier; // Nullable, for Receipts


    @ManyToOne
    @JoinColumn(name = "from_warehouse_id")
    private Warehouse fromWarehouse; // Null for Receipts

    @ManyToOne
    @JoinColumn(name = "to_warehouse_id")
    private Warehouse toWarehouse;   // Null for Deliveries

    private Integer quantity;

    private LocalDateTime timestamp;
    private String status; // "DONE", "PENDING", "DRAFT", "WAITING", "READY", "CANCELED"
    
    // For deliveries
    private String customerName;
    private String shippingAddress;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
        if(this.status == null) this.status = "DONE";
    }
}
