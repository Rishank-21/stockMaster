package com.example.hackathon.controllers;

import com.example.hackathon.models.Supplier;
import com.example.hackathon.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin("*")
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepo;

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierRepo.findAll();
    }

    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierRepo.save(supplier);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplier(@PathVariable Long id) {
        return supplierRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @RequestBody Supplier updatedSupplier) {
        return supplierRepo.findById(id)
                .map(supplier -> {
                    supplier.setName(updatedSupplier.getName());
                    supplier.setContactInfo(updatedSupplier.getContactInfo());
                    supplier.setEmail(updatedSupplier.getEmail());
                    supplier.setAddress(updatedSupplier.getAddress());
                    return ResponseEntity.ok(supplierRepo.save(supplier));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        try {
            if (!supplierRepo.existsById(id)) {
                return ResponseEntity.status(404).body("Supplier not found");
            }

            // Check if supplier is used in any stock movements
            // This would require StockMovementRepository injection if we want to validate
            
            supplierRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Cannot delete supplier: " + e.getMessage());
        }
    }
}
