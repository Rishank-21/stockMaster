package com.example.hackathon.controllers;

import com.example.hackathon.models.Warehouse;
import com.example.hackathon.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin("*")
public class WarehouseController {
    
    @Autowired
    private WarehouseRepository repo;
    
    @PostMapping
    public Warehouse create(@RequestBody Warehouse w) {
        return repo.save(w);
    }
    
    @GetMapping
    public List<Warehouse> getAll() {
        return repo.findAll();
    }
}
