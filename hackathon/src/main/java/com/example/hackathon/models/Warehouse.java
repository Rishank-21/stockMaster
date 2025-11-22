package com.example.hackathon.models;



import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., "Main Warehouse", "Production Floor"
    private String location;
    private Integer capacity; // Optional warehouse capacity
}
