package com.example.hackathon.controllers;

import com.example.hackathon.models.Product;
import com.example.hackathon.repository.ProductRepository;
import com.example.hackathon.repository.StockRepository;
import com.example.hackathon.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductController {
    
    @Autowired
    private ProductRepository repo;
    
    @Autowired
    private StockRepository stockRepo;
    
    @Autowired
    private StockMovementRepository movementRepo;
    
    @PostMapping
    public Product create(@RequestBody Product p) {
        return repo.save(p);
    }
    
    @GetMapping
    public List<Product> getAll() {
        return repo.findAll();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product p) {
        return repo.findById(id)
            .map(existing -> {
                p.setId(id);
                return ResponseEntity.ok(repo.save(p));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repo.findById(id)
            .map(p -> {
                // Check if product has non-zero inventory
                long stockCount = stockRepo.countByProductIdWithStock(id);
                if (stockCount > 0) {
                    return ResponseEntity.badRequest().body(java.util.Map.of(
                        "error", "Cannot delete product",
                        "message", "Product inventory exists! Please zero out the stock in all warehouses before deleting this product."
                    ));
                }
                
                try {
                    // Delete all zero-stock inventory records for this product
                    stockRepo.deleteByProduct(p);
                    
                    // Delete the product
                    repo.delete(p);
                    return ResponseEntity.ok(java.util.Map.of("message", "Product deleted successfully"));
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(java.util.Map.of(
                        "error", "Cannot delete product",
                        "message", "An error occurred while deleting the product: " + e.getMessage()
                    ));
                }
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
