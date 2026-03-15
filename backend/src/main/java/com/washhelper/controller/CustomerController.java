package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Customer;
import com.washhelper.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "客户管理", description = "客户相关接口")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    @Operation(summary = "获取客户列表")
    public PageResponse<Customer> getCustomers(
            @Parameter(description = "会员类型筛选")
            @RequestParam(required = false) String type,
            @Parameter(description = "关键字搜索")
            @RequestParam(required = false) String search,
            @Parameter(description = "页码，从1开始")
            @RequestParam(defaultValue = "1") int page) {
        return customerService.getCustomers(type, search, page, 20);
    }
    
    @PostMapping
    @Operation(summary = "新建客户")
    public ApiResponse<Customer> createCustomer(@RequestBody Customer customer) {
        Customer created = customerService.createCustomer(customer);
        return ApiResponse.success(created.getCustomerId());
    }
    
    @PostMapping("/{id}/contact")
    @Operation(summary = "联系客户")
    public ApiResponse<Void> contactCustomer(@PathVariable Long id) {
        customerService.contactCustomer(id);
        return ApiResponse.success();
    }
}
