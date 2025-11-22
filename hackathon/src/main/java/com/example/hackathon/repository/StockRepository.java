package com.example.hackathon.repository;

import com.example.hackathon.models.StockInventory;
import com.example.hackathon.models.Product;
import com.example.hackathon.models.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockInventory, Long> {
    Optional<StockInventory> findByProductAndWarehouse(Product product, Warehouse warehouse);
}
