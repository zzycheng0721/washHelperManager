package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Order;
import com.washhelper.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单相关接口")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    @Operation(summary = "获取订单列表")
    public PageResponse<Order> getOrders(
            @Parameter(description = "订单状态筛选") 
            @RequestParam(required = false) String status,
            @Parameter(description = "关键字搜索") 
            @RequestParam(required = false) String search,
            @Parameter(description = "页码，从1开始") 
            @RequestParam(defaultValue = "1") int page) {
        return orderService.getOrders(status, search, page, 20);
    }
    
    @PostMapping
    @Operation(summary = "新建订单")
    public ApiResponse<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return ApiResponse.success(created.getOrderId());
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态")
    public ApiResponse<Void> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        orderService.updateOrderStatus(id, request.get("status"));
        return ApiResponse.success();
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单")
    public ApiResponse<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success();
    }
    
    @PostMapping("/{id}/print")
    @Operation(summary = "打印订单标签")
    public ApiResponse<Void> printOrder(@PathVariable Long id) {
        orderService.printOrder(id);
        return ApiResponse.success(null, "print queued");
    }
}
