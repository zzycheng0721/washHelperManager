package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Inventory;
import com.washhelper.service.InventoryService;
import com.washhelper.util.TenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public PageResponse<Inventory> getInventory(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader) {
        return inventoryService.getInventory(TenantUtil.resolve(shopId, shopIdHeader));
    }

    @PostMapping
    public ApiResponse<Inventory> createInventory(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestBody Inventory inventory) {
        Inventory created = inventoryService.createInventory(TenantUtil.resolve(shopId, shopIdHeader), inventory);
        return ApiResponse.success(String.valueOf(created.getId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateInventory(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id,
            @RequestBody Inventory inventory) {
        inventoryService.updateInventory(TenantUtil.resolve(shopId, shopIdHeader), id, inventory);
        return ApiResponse.success();
    }
}
