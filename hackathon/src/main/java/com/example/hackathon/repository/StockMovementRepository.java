package com.example.hackathon.repository;

import com.example.hackathon.models.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT m FROM StockMovement m WHERE " +
            "(:type IS NULL OR m.type = :type) AND " +
            "(:warehouseId IS NULL OR m.fromWarehouse.id = :warehouseId OR m.toWarehouse.id = :warehouseId) AND " +
            "(:productId IS NULL OR m.product.id = :productId) AND " +
            "(:status IS NULL OR m.status = :status) AND " +
            "(:category IS NULL OR m.product.category = :category)")
    java.util.List<StockMovement> findByFilters(String type, Long warehouseId, Long productId, String status, String category);
    
    long countByProductId(Long productId);
    
    @Query("SELECT COUNT(m) FROM StockMovement m WHERE m.fromWarehouse.id = :warehouseId OR m.toWarehouse.id = :warehouseId")
    long countByWarehouseId(Long warehouseId);
}
