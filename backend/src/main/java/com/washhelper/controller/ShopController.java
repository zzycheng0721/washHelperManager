package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getShopInfo() {
        return ApiResponse.success(shopService.getShopInfo());
    }

    @PutMapping("/info")
    public ApiResponse<Map<String, Object>> updateShopInfo(@RequestBody Map<String, Object> request) {
        return ApiResponse.success(shopService.updateShopInfo(request));
    }

    @GetMapping("/operating-hours")
    public ApiResponse<Map<String, Object>> getOperatingHours() {
        return ApiResponse.success(shopService.getOperatingHours());
    }

    @PutMapping("/operating-hours")
    public ApiResponse<Void> updateOperatingHours(@RequestBody Map<String, Object> request) {
        shopService.updateOperatingHours(request);
        return ApiResponse.success();
    }
}
