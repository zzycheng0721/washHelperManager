package com.washhelper.service;

import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Inventory;
import com.washhelper.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    public PageResponse<Inventory> getInventory() {
        List<Inventory> items = inventoryRepository.findAll();
        return new PageResponse<>(items, 1, items.size(), items.size());
    }
    
    @Transactional
    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
    
    @Transactional
    public Inventory updateInventory(Long id, Inventory inventory) {
        Inventory existing = inventoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        existing.setName(inventory.getName());
        existing.setCategory(inventory.getCategory());
        existing.setAvailable(inventory.getAvailable());
        existing.setTotal(inventory.getTotal());
        existing.setUnit(inventory.getUnit());
        existing.setAlertThreshold(inventory.getAlertThreshold());
        existing.setNotes(inventory.getNotes());
        
        return inventoryRepository.save(existing);
    }
}