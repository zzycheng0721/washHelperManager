package com.washhelper.repository;

import com.washhelper.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByShopIdOrderByIdAsc(Long shopId);
    Optional<Inventory> findByShopIdAndId(Long shopId, Long id);
}
