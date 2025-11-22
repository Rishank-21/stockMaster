package com.example.hackathon.controllers;

import com.example.hackathon.models.Warehouse;
import com.example.hackathon.repository.WarehouseRepository;
import com.example.hackathon.repository.StockRepository;
import com.example.hackathon.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin("*")
public class WarehouseController {
    
    @Autowired
    private WarehouseRepository repo;
    
    @Autowired
    private StockRepository stockRepo;
    
    @Autowired
    private StockMovementRepository movementRepo;
    
    @PostMapping
    public Warehouse create(@RequestBody Warehouse w) {
        return repo.save(w);
    }
    
    @GetMapping
    public List<Warehouse> getAll() {
        return repo.findAll();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Warehouse> update(@PathVariable Long id, @RequestBody Warehouse w) {
        return repo.findById(id)
            .map(existing -> {
                w.setId(id);
                return ResponseEntity.ok(repo.save(w));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repo.findById(id)
            .map(w -> {
                // Check if warehouse has non-zero inventory
                long stockCount = stockRepo.countByWarehouseIdWithStock(id);
                if (stockCount > 0) {
                    return ResponseEntity.badRequest().body(java.util.Map.of(
                        "error", "Cannot delete warehouse",
                        "message", "Warehouse has products in stock! Please empty the warehouse by transferring or adjusting all inventory to zero before deleting."
                    ));
                }
                
                try {
                    // Delete all zero-stock inventory records for this warehouse
                    stockRepo.deleteByWarehouse(w);
                    
                    // Delete the warehouse
                    repo.delete(w);
                    return ResponseEntity.ok(java.util.Map.of("message", "Warehouse deleted successfully"));
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(java.util.Map.of(
                        "error", "Cannot delete warehouse",
                        "message", "An error occurred while deleting the warehouse: " + e.getMessage()
                    ));
                }
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
