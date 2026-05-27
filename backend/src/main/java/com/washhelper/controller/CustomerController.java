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

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "Customer APIs")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get customer list")
    public PageResponse<Customer> getCustomers(
            @Parameter(description = "Member type filter")
            @RequestParam(required = false) String type,
            @Parameter(description = "Keyword search")
            @RequestParam(required = false) String search,
            @Parameter(description = "Page number, starts from 1")
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return customerService.getCustomers(type, search, page, pageSize);
    }

    @PostMapping
    @Operation(summary = "Create customer")
    public ApiResponse<Customer> createCustomer(@RequestBody Customer customer) {
        Customer created = customerService.createCustomer(customer);
        return ApiResponse.success(created.getCustomerId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer detail")
    public ApiResponse<Map<String, Object>> getCustomer(@PathVariable String id) {
        return ApiResponse.success(customerService.getCustomerDetail(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ApiResponse<Map<String, Object>> updateCustomer(@PathVariable String id,
                                                           @RequestBody Map<String, Object> request) {
        return ApiResponse.success(customerService.updateCustomer(id, request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch customer")
    public ApiResponse<Map<String, Object>> patchCustomer(@PathVariable String id,
                                                          @RequestBody Map<String, Object> request) {
        return ApiResponse.success(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer")
    public ApiResponse<Void> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/balance")
    @Operation(summary = "Update customer balance")
    public ApiResponse<Map<String, Object>> updateBalance(@PathVariable String id,
                                                          @RequestBody Map<String, Object> request) {
        Object balance = request.get("balance");
        BigDecimal value = balance instanceof Number number
                ? BigDecimal.valueOf(number.doubleValue())
                : new BigDecimal(String.valueOf(balance == null ? "0" : balance));
        return ApiResponse.success(customerService.updateBalance(id, value));
    }

    @PostMapping("/{id}/contact")
    @Operation(summary = "Contact customer")
    public ApiResponse<Void> contactCustomer(@PathVariable Long id) {
        customerService.contactCustomer(id);
        return ApiResponse.success();
    }
}
