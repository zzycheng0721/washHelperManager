package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.service.ShopService;
import com.washhelper.util.TenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getShopInfo(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader) {
        return ApiResponse.success(shopService.getShopInfo(TenantUtil.resolve(shopId, shopIdHeader)));
    }

    @PutMapping("/info")
    public ApiResponse<Map<String, Object>> updateShopInfo(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestBody Map<String, Object> request) {
        return ApiResponse.success(shopService.updateShopInfo(TenantUtil.resolve(shopId, shopIdHeader), request));
    }

    @GetMapping("/operating-hours")
    public ApiResponse<Map<String, Object>> getOperatingHours(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader) {
        return ApiResponse.success(shopService.getOperatingHours(TenantUtil.resolve(shopId, shopIdHeader)));
    }

    @PutMapping("/operating-hours")
    public ApiResponse<Void> updateOperatingHours(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestBody Map<String, Object> request) {
        shopService.updateOperatingHours(TenantUtil.resolve(shopId, shopIdHeader), request);
        return ApiResponse.success();
    }
}
