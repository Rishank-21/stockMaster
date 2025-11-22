package com.example.hackathon.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String sku;

    private String category;
    private String unitOfMeasure; // e.g., "kg", "units"

    private Double price;

    private Integer minStockLevel; // For low stock alerts

    @Transient
    private Integer initialStock; // Used only during creation
}