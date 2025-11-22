package com.example.hackathon.controllers;

import com.example.hackathon.models.Product;
import com.example.hackathon.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductController {
    
    @Autowired
    private ProductRepository repo;
    
    @PostMapping
    public Product create(@RequestBody Product p) {
        return repo.save(p);
    }
    
    @GetMapping
    public List<Product> getAll() {
        return repo.findAll();
    }
}
