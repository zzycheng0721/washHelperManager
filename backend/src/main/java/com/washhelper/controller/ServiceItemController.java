package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.ServiceItem;
import com.washhelper.service.ServiceItemService;
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
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int pageSize) {
        return serviceItemService.search(category, keyword, enabled, page, pageSize);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getService(@PathVariable Long id) {
        return ApiResponse.success(serviceItemService.get(id));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> createService(@RequestBody Map<String, Object> request) {
        ServiceItem created = serviceItemService.create(request);
        ApiResponse<Map<String, Object>> response = ApiResponse.success(serviceItemService.toMap(created));
        response.setId(String.valueOf(created.getId()));
        return response;
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateService(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        serviceItemService.update(id, request);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> patchService(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        serviceItemService.update(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteService(@PathVariable Long id) {
        serviceItemService.delete(id);
        return ApiResponse.success();
    }
}
