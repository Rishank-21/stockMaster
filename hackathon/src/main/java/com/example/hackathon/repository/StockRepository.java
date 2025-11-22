package com.example.hackathon.repository;

import com.example.hackathon.models.StockInventory;
import com.example.hackathon.models.Product;
import com.example.hackathon.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<StockInventory, Long> {
    Optional<StockInventory> findByProductAndWarehouse(Product product, Warehouse warehouse);
    
    List<StockInventory> findByProduct(Product product);
    
    List<StockInventory> findByWarehouse(Warehouse warehouse);
    
    @Query("SELECT COUNT(s) FROM StockInventory s WHERE s.product.id = :productId AND s.quantity > 0")
    long countByProductIdWithStock(Long productId);
    
    @Query("SELECT COUNT(s) FROM StockInventory s WHERE s.warehouse.id = :warehouseId AND s.quantity > 0")
    long countByWarehouseIdWithStock(Long warehouseId);
    
    @Transactional
    @Modifying
    void deleteByProduct(Product product);
    
    @Transactional
    @Modifying
    void deleteByWarehouse(Warehouse warehouse);
}
