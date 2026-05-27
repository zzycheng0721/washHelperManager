package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Order;
import com.washhelper.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Order APIs")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Get order list")
    public PageResponse<Order> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return orderService.getOrders(status, search, page, pageSize);
    }

    @PostMapping
    @Operation(summary = "Create order")
    public ApiResponse<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return ApiResponse.success(created.getOrderId());
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status")
    public ApiResponse<Void> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        orderService.updateOrderStatus(id, request.get("status"));
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order")
    public ApiResponse<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/print")
    @Operation(summary = "Print order label")
    public ApiResponse<Void> printOrder(@PathVariable Long id) {
        orderService.printOrder(id);
        return ApiResponse.success(null, "print queued");
    }
}
