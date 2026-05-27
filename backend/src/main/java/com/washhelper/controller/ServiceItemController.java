package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.ServiceItem;
import com.washhelper.service.ServiceItemService;
import com.washhelper.util.TenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class ServiceItemController {
    @Autowired
    private ServiceItemService serviceItemService;

    @GetMapping
    public PageResponse<Map<String, Object>> getServices(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int pageSize) {
        return serviceItemService.search(TenantUtil.resolve(shopId, shopIdHeader), category, keyword, enabled, page, pageSize);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getService(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id) {
        return ApiResponse.success(serviceItemService.get(TenantUtil.resolve(shopId, shopIdHeader), id));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> createService(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestBody Map<String, Object> request) {
        ServiceItem created = serviceItemService.create(TenantUtil.resolve(shopId, shopIdHeader), request);
        ApiResponse<Map<String, Object>> response = ApiResponse.success(serviceItemService.toMap(created));
        response.setId(String.valueOf(created.getId()));
        return response;
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateService(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        serviceItemService.update(TenantUtil.resolve(shopId, shopIdHeader), id, request);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> patchService(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        serviceItemService.update(TenantUtil.resolve(shopId, shopIdHeader), id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteService(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id) {
        serviceItemService.delete(TenantUtil.resolve(shopId, shopIdHeader), id);
        return ApiResponse.success();
    }
}
