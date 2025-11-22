package com.example.hackathon.models;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password; // In real app, store encrypted!
    private String email;
    private String firstName;
    private String lastName;

    // Roles: "MANAGER", "STAFF"
    private String role;
}