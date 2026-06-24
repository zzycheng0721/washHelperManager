package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Order;
import com.washhelper.service.OrderService;
import com.washhelper.util.TenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public PageResponse<Order> getOrders(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return orderService.getOrders(TenantUtil.resolve(shopId, shopIdHeader), status, search, page, pageSize);
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestParam(defaultValue = "today") String range) {
        return ApiResponse.success(orderService.stats(TenantUtil.resolve(shopId, shopIdHeader), range));
    }
    @PostMapping
    public ApiResponse<Order> createOrder(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestBody Order order) {
        Order created = orderService.createOrder(TenantUtil.resolve(shopId, shopIdHeader), order);
        return ApiResponse.success(created.getOrderId());
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateOrderStatus(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        orderService.updateOrderStatus(TenantUtil.resolve(shopId, shopIdHeader), id, request.get("status"));
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrder(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id) {
        orderService.deleteOrder(TenantUtil.resolve(shopId, shopIdHeader), id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/print")
    public ApiResponse<Void> printOrder(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id) {
        orderService.printOrder(TenantUtil.resolve(shopId, shopIdHeader), id);
        return ApiResponse.success(null, "print queued");
    }
}
