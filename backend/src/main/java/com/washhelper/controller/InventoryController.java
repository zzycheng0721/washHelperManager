package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Inventory;
import com.washhelper.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "库存管理", description = "库存相关接口")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    @GetMapping
    @Operation(summary = "获取库存列表")
    public PageResponse<Inventory> getInventory() {
        return inventoryService.getInventory();
    }
    
    @PostMapping
    @Operation(summary = "新建库存条目")
    public ApiResponse<Inventory> createInventory(@RequestBody Inventory inventory) {
        Inventory created = inventoryService.createInventory(inventory);
        return ApiResponse.success(String.valueOf(created.getId()));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新库存条目")
    public ApiResponse<Void> updateInventory(@PathVariable Long id, @RequestBody Inventory inventory) {
        inventoryService.updateInventory(id, inventory);
        return ApiResponse.success();
    }
}
