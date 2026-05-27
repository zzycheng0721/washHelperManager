package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Customer;
import com.washhelper.service.CustomerService;
import com.washhelper.util.TenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public PageResponse<Customer> getCustomers(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return customerService.getCustomers(TenantUtil.resolve(shopId, shopIdHeader), type, search, page, pageSize);
    }

    @PostMapping
    public ApiResponse<Customer> createCustomer(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestBody Customer customer) {
        Customer created = customerService.createCustomer(TenantUtil.resolve(shopId, shopIdHeader), customer);
        return ApiResponse.success(created.getCustomerId());
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getCustomer(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable String id) {
        return ApiResponse.success(customerService.getCustomerDetail(TenantUtil.resolve(shopId, shopIdHeader), id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> updateCustomer(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        return ApiResponse.success(customerService.updateCustomer(TenantUtil.resolve(shopId, shopIdHeader), id, request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<Map<String, Object>> patchCustomer(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        return ApiResponse.success(customerService.updateCustomer(TenantUtil.resolve(shopId, shopIdHeader), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCustomer(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable String id) {
        customerService.deleteCustomer(TenantUtil.resolve(shopId, shopIdHeader), id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/balance")
    public ApiResponse<Map<String, Object>> updateBalance(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        Object balance = request.get("balance");
        BigDecimal value = balance instanceof Number number
                ? BigDecimal.valueOf(number.doubleValue())
                : new BigDecimal(String.valueOf(balance == null ? "0" : balance));
        return ApiResponse.success(customerService.updateBalance(TenantUtil.resolve(shopId, shopIdHeader), id, value));
    }

    @PostMapping("/{id}/contact")
    public ApiResponse<Void> contactCustomer(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id) {
        customerService.contactCustomer(TenantUtil.resolve(shopId, shopIdHeader), id);
        return ApiResponse.success();
    }
}
