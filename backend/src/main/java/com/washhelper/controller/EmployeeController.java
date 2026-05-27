package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Employee;
import com.washhelper.service.EmployeeService;
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
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return employeeService.search(keyword, role, status, page, pageSize);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getEmployee(@PathVariable Long id) {
        return ApiResponse.success(employeeService.get(id));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> createEmployee(@RequestBody Map<String, Object> request) {
        Employee created = employeeService.create(request);
        ApiResponse<Map<String, Object>> response = ApiResponse.success(employeeService.toMap(created));
        response.setId(String.valueOf(created.getId()));
        return response;
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateEmployee(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        employeeService.update(id, request);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> patchEmployee(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        employeeService.update(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.delete(id);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}/password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        employeeService.resetPassword(id, String.valueOf(request.get("password")));
        return ApiResponse.success();
    }
}
