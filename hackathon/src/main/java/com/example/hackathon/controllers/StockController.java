package com.example.hackathon.controllers;

import com.example.hackathon.models.StockInventory;
import com.example.hackathon.models.StockMovement;
import com.example.hackathon.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin("*")
public class StockController {

    @Autowired
    private StockService stockService;

    // Get all current stock levels
    @GetMapping("/inventory")
    public List<StockInventory> getInventory() {
        return stockService.getAllStock();
    }

    // Receive Goods (Vendor -> Warehouse)
    @PostMapping("/receipt")
    public StockMovement addReceipt(@RequestBody com.example.hackathon.dto.StockMovementDTO dto) {
        return stockService.createReceipt(dto.getProductId(), dto.getToWarehouseId(), dto.getSupplierId(), dto.getQuantity(), dto.getStatus());
    }

    // Deliver Goods (Warehouse -> Customer)
    @PostMapping("/delivery")
    public StockMovement addDelivery(@RequestBody com.example.hackathon.dto.StockMovementDTO dto) {
        return stockService.createDelivery(dto.getProductId(), dto.getFromWarehouseId(), dto.getQuantity(), 
                dto.getStatus(), dto.getCustomerName(), dto.getShippingAddress());
    }

    // Internal Transfer (Warehouse A -> Warehouse B)
    @PostMapping("/transfer")
    public StockMovement transferStock(@RequestBody com.example.hackathon.dto.StockMovementDTO dto) {
        return stockService.createTransfer(dto.getProductId(), dto.getFromWarehouseId(), dto.getToWarehouseId(), dto.getQuantity());
    }

    // Stock Adjustment
    @PostMapping("/adjustment")
    public StockMovement adjustStock(@RequestBody com.example.hackathon.dto.StockMovementDTO dto) {
        return stockService.createAdjustment(dto.getProductId(), dto.getToWarehouseId(), dto.getQuantity());
    }

    // Dashboard Stats
    @GetMapping("/dashboard/stats")
    public com.example.hackathon.dto.DashboardStatsDTO getDashboardStats() {
        return stockService.getDashboardStats();
    }

    // Move History with Filters
    @GetMapping("/history")
    public List<StockMovement> getHistory(@RequestParam(required = false) String type,
                                          @RequestParam(required = false) Long warehouseId,
                                          @RequestParam(required = false) Long productId,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) String category) {
        return stockService.getMovements(type, warehouseId, productId, status, category);
    }

    // Validate Movement (change status from PENDING to DONE)
    @PostMapping("/validate/{id}")
    public StockMovement validateMovement(@PathVariable Long id) {
        return stockService.validateMovement(id);
    }

    // Update Movement Status
    @PutMapping("/movement/{id}/status")
    public StockMovement updateMovementStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String status = body.get("status");
        return stockService.updateMovementStatus(id, status);
    }

    // Delete Movement (History)
    @DeleteMapping("/movement/{id}")
    public org.springframework.http.ResponseEntity<?> deleteMovement(@PathVariable Long id) {
        try {
            stockService.deleteMovement(id);
            return org.springframework.http.ResponseEntity.noContent().build();
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(400).body("Cannot delete movement: " + e.getMessage());
        }
    }

    // Get Low Stock Items
    @GetMapping("/low-stock")
    public List<StockInventory> getLowStockItems() {
        return stockService.getLowStockItems();
    }
}
