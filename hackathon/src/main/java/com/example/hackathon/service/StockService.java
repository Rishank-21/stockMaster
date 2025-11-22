package com.example.hackathon.service;

import com.example.hackathon.models.Product;
import com.example.hackathon.models.StockInventory;
import com.example.hackathon.models.StockMovement;
import com.example.hackathon.models.Supplier;
import com.example.hackathon.models.Warehouse;
import com.example.hackathon.repository.ProductRepository;
import com.example.hackathon.repository.StockMovementRepository;
import com.example.hackathon.repository.StockRepository;
import com.example.hackathon.repository.SupplierRepository;
import com.example.hackathon.repository.WarehouseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepo;
    @Autowired
    private StockMovementRepository movementRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private WarehouseRepository warehouseRepo;

    @Autowired
    private SupplierRepository supplierRepo;

    // 1. RECEIPT (Incoming Goods)
    @Transactional
    public StockMovement createReceipt(Long productId, Long warehouseId, Long supplierId, Integer quantity, String status) {
        Product product = productRepo.findById(productId).orElseThrow();
        Warehouse warehouse = warehouseRepo.findById(warehouseId).orElseThrow();
        Supplier supplier = supplierId != null ? supplierRepo.findById(supplierId).orElse(null) : null;

        // Log Movement
        StockMovement move = new StockMovement();
        move.setType("RECEIPT");
        move.setProduct(product);
        move.setToWarehouse(warehouse);
        move.setSupplier(supplier);
        move.setQuantity(quantity);
        move.setStatus(status != null ? status : "DONE");
        
        // Only update inventory if status is DONE
        if ("DONE".equals(move.getStatus())) {
            updateStock(product, warehouse, quantity);
        }
        
        return movementRepo.save(move);
    }

    // 2. DELIVERY (Outgoing Goods)
    @Transactional
    public StockMovement createDelivery(Long productId, Long warehouseId, Integer quantity, String status) {
        Product product = productRepo.findById(productId).orElseThrow();
        Warehouse warehouse = warehouseRepo.findById(warehouseId).orElseThrow();

        // Log Movement first
        StockMovement move = new StockMovement();
        move.setType("DELIVERY");
        move.setProduct(product);
        move.setFromWarehouse(warehouse);
        move.setQuantity(quantity);
        move.setStatus(status != null ? status : "DONE");
        
        // Only validate and update stock if status is DONE
        if ("DONE".equals(move.getStatus())) {
            // Check if enough stock exists
            StockInventory inventory = stockRepo.findByProductAndWarehouse(product, warehouse)
                    .orElseThrow(() -> new RuntimeException("No stock found"));

            if (inventory.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock");
            }

            // Update Inventory (Subtract)
            updateStock(product, warehouse, -quantity);
        }
        
        return movementRepo.save(move);
    }

    // 3. INTERNAL TRANSFER (Warehouse A -> Warehouse B)
    @Transactional
    public StockMovement createTransfer(Long productId, Long fromId, Long toId, Integer quantity) {
        Product product = productRepo.findById(productId).orElseThrow();
        Warehouse fromWh = warehouseRepo.findById(fromId).orElseThrow();
        Warehouse toWh = warehouseRepo.findById(toId).orElseThrow();

        // Deduct from Source
        StockInventory sourceInv = stockRepo.findByProductAndWarehouse(product, fromWh)
                .orElseThrow(() -> new RuntimeException("No stock in source"));
        if (sourceInv.getQuantity() < quantity) throw new RuntimeException("Insufficient stock");

        updateStock(product, fromWh, -quantity);

        // Add to Destination
        updateStock(product, toWh, quantity);

        // Log Movement
        StockMovement move = new StockMovement();
        move.setType("TRANSFER");
        move.setProduct(product);
        move.setFromWarehouse(fromWh);
        move.setToWarehouse(toWh);
        move.setQuantity(quantity);
        return movementRepo.save(move);
    }

    // 4. STOCK ADJUSTMENT
    @Transactional
    public StockMovement createAdjustment(Long productId, Long warehouseId, Integer quantity) {
        Product product = productRepo.findById(productId).orElseThrow();
        Warehouse warehouse = warehouseRepo.findById(warehouseId).orElseThrow();

        // Get current stock
        StockInventory inventory = stockRepo.findByProductAndWarehouse(product, warehouse)
                .orElse(new StockInventory());

        int currentQty = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
        int adjustment = quantity - currentQty; // Difference to apply

        // Update Inventory to match counted quantity
        updateStock(product, warehouse, adjustment);

        // Log Movement
        StockMovement move = new StockMovement();
        move.setType("ADJUSTMENT");
        move.setProduct(product);
        move.setToWarehouse(warehouse); // Or fromWarehouse depending on +/-, simplified here
        move.setQuantity(adjustment);
        return movementRepo.save(move);
    }

    // Helper to update stock levels safely
    private void updateStock(Product product, Warehouse warehouse, Integer change) {
        StockInventory inventory = stockRepo.findByProductAndWarehouse(product, warehouse)
                .orElse(new StockInventory());

        if (inventory.getProduct() == null) {
            inventory.setProduct(product);
            inventory.setWarehouse(warehouse);
            inventory.setQuantity(0);
        }

        inventory.setQuantity(inventory.getQuantity() + change);
        stockRepo.save(inventory);
    }

    public List<StockInventory> getAllStock() {
        return stockRepo.findAll();
    }

    public com.example.hackathon.dto.DashboardStatsDTO getDashboardStats() {
        com.example.hackathon.dto.DashboardStatsDTO stats = new com.example.hackathon.dto.DashboardStatsDTO();
        stats.setTotalProducts(productRepo.count());

        // Low stock logic (simplified: count where quantity < 10)
        long lowStockCount = stockRepo.findAll().stream()
                .filter(inv -> inv.getQuantity() < 10)
                .count();
        stats.setLowStockItems(lowStockCount);

        // Pending receipts/deliveries
        long pendingReceipts = movementRepo.findAll().stream()
                .filter(m -> "RECEIPT".equals(m.getType()) && "PENDING".equals(m.getStatus()))
                .count();
        stats.setPendingReceipts(pendingReceipts);

        long pendingDeliveries = movementRepo.findAll().stream()
                .filter(m -> "DELIVERY".equals(m.getType()) && "PENDING".equals(m.getStatus()))
                .count();
        stats.setPendingDeliveries(pendingDeliveries);

        return stats;
    }

    public List<StockMovement> getMovements(String type, Long warehouseId, Long productId, String status, String category) {
        return movementRepo.findByFilters(type, warehouseId, productId, status, category);
    }

    // Validate a pending movement (change status from PENDING to DONE and update stock)
    @Transactional
    public StockMovement validateMovement(Long movementId) {
        StockMovement move = movementRepo.findById(movementId)
                .orElseThrow(() -> new RuntimeException("Movement not found"));

        if ("DONE".equals(move.getStatus())) {
            throw new RuntimeException("Movement already validated");
        }

        move.setStatus("DONE");

        // Apply stock changes based on type
        switch (move.getType()) {
            case "RECEIPT":
                updateStock(move.getProduct(), move.getToWarehouse(), move.getQuantity());
                break;
            case "DELIVERY":
                // Validate stock availability
                StockInventory inv = stockRepo.findByProductAndWarehouse(move.getProduct(), move.getFromWarehouse())
                        .orElseThrow(() -> new RuntimeException("No stock found"));
                if (inv.getQuantity() < move.getQuantity()) {
                    throw new RuntimeException("Insufficient stock");
                }
                updateStock(move.getProduct(), move.getFromWarehouse(), -move.getQuantity());
                break;
            case "TRANSFER":
                StockInventory sourceInv = stockRepo.findByProductAndWarehouse(move.getProduct(), move.getFromWarehouse())
                        .orElseThrow(() -> new RuntimeException("No stock in source"));
                if (sourceInv.getQuantity() < move.getQuantity()) {
                    throw new RuntimeException("Insufficient stock");
                }
                updateStock(move.getProduct(), move.getFromWarehouse(), -move.getQuantity());
                updateStock(move.getProduct(), move.getToWarehouse(), move.getQuantity());
                break;
            case "ADJUSTMENT":
                StockInventory inventory = stockRepo.findByProductAndWarehouse(move.getProduct(), move.getToWarehouse())
                        .orElse(new StockInventory());
                int currentQty = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
                int adjustment = move.getQuantity() - currentQty;
                updateStock(move.getProduct(), move.getToWarehouse(), adjustment);
                break;
        }

        return movementRepo.save(move);
    }

    // Get list of low stock items
    public List<StockInventory> getLowStockItems() {
        return stockRepo.findAll().stream()
                .filter(inv -> {
                    Product product = inv.getProduct();
                    Integer minLevel = product.getMinStockLevel();
                    return minLevel != null && inv.getQuantity() < minLevel;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
