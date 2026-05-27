package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Employee;
import com.washhelper.service.EmployeeService;
import com.washhelper.util.TenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public PageResponse<Map<String, Object>> getEmployees(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return employeeService.search(TenantUtil.resolve(shopId, shopIdHeader), keyword, role, status, page, pageSize);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getEmployee(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id) {
        return ApiResponse.success(employeeService.get(TenantUtil.resolve(shopId, shopIdHeader), id));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> createEmployee(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestBody Map<String, Object> request) {
        Employee created = employeeService.create(TenantUtil.resolve(shopId, shopIdHeader), request);
        ApiResponse<Map<String, Object>> response = ApiResponse.success(employeeService.toMap(created));
        response.setId(String.valueOf(created.getId()));
        return response;
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateEmployee(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        employeeService.update(TenantUtil.resolve(shopId, shopIdHeader), id, request);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> patchEmployee(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        employeeService.update(TenantUtil.resolve(shopId, shopIdHeader), id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEmployee(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id) {
        employeeService.delete(TenantUtil.resolve(shopId, shopIdHeader), id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/password")
    public ApiResponse<Void> resetPassword(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        employeeService.resetPassword(TenantUtil.resolve(shopId, shopIdHeader), id, String.valueOf(request.get("password")));
        return ApiResponse.success();
    }
}
