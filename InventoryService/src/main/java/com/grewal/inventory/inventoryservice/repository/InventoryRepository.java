package com.grewal.inventory.inventoryservice.repository;

import com.grewal.inventory.inventoryservice.dto.InventoryDto;
import com.grewal.inventory.inventoryservice.model.Inventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    Optional<Inventory> findBySkuCode(String skuCode);
    Optional<Inventory> findBySkuCodeAndApplicationId(String skuCode,String application);

    List<Inventory> findBySkuCodeIn(List<String> skuCode);

    List<Inventory> findAllByApplicationId(String application);
    Optional<List<Inventory>> findAllByApplicationId(String application, Pageable pageable);


}