package com.washhelper.service;

import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Employee;
import com.washhelper.entity.EmployeePermission;
import com.washhelper.repository.EmployeePermissionRepository;
import com.washhelper.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeePermissionRepository employeePermissionRepository;

    public PageResponse<Map<String, Object>> search(Long shopId, String keyword, String role, String status, int page, int pageSize) {
        int normalizedPage = Math.max(page, 1);
        Pageable pageable = PageRequest.of(normalizedPage - 1, pageSize);
        Page<Employee> employeePage = employeeRepository.search(shopId, emptyToNull(keyword), emptyToNull(role), emptyToNull(status), pageable);
        List<Map<String, Object>> items = employeePage.getContent().stream().map(this::toMap).toList();
        return new PageResponse<>(items, normalizedPage, pageSize, employeePage.getTotalElements());
    }

    public Map<String, Object> get(Long shopId, Long id) {
        return toMap(find(shopId, id));
    }

    @Transactional
    public Employee create(Long shopId, Map<String, Object> request) {
        Employee employee = new Employee();
        employee.setShopId(shopId);
        apply(employee, request);
        employee.setPasswordHash(hashPassword(String.valueOf(request.getOrDefault("password", "123456"))));
        Employee saved = employeeRepository.save(employee);
        savePermissions(shopId, saved.getId(), request.get("permissions"));
        return saved;
    }

    @Transactional
    public void update(Long shopId, Long id, Map<String, Object> request) {
        Employee employee = find(shopId, id);
        apply(employee, request);
        employeeRepository.save(employee);
        if (request.containsKey("permissions")) {
            savePermissions(shopId, id, request.get("permissions"));
        }
    }

    @Transactional
    public void delete(Long shopId, Long id) {
        Employee employee = find(shopId, id);
        employee.setStatus("inactive");
        employeeRepository.save(employee);
    }

    @Transactional
    public void resetPassword(Long shopId, Long id, String password) {
        Employee employee = find(shopId, id);
        employee.setPasswordHash(hashPassword(password));
        employeeRepository.save(employee);
    }

    public Map<String, Object> toMap(Employee employee) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", employee.getId());
        data.put("shopId", employee.getShopId());
        data.put("name", employee.getName());
        data.put("role", employee.getRole());
        data.put("phone", employee.getPhone());
        data.put("status", employee.getStatus());
        data.put("avatarUrl", employee.getAvatarUrl());
        data.put("permissions", permissions(employee.getShopId(), employee.getId()));
        data.put("createdAt", employee.getCreatedAt());
        data.put("updatedAt", employee.getUpdatedAt());
        return data;
    }

    private void apply(Employee employee, Map<String, Object> request) {
        if (request.containsKey("name")) employee.setName(asString(request.get("name")));
        if (request.containsKey("phone")) employee.setPhone(asString(request.get("phone")));
        if (request.containsKey("role")) employee.setRole(asString(request.get("role")));
        if (request.containsKey("status")) employee.setStatus(asString(request.get("status")));
        if (request.containsKey("avatarUrl")) employee.setAvatarUrl(asString(request.get("avatarUrl")));
    }

    private void savePermissions(Long shopId, Long employeeId, Object permissionsObj) {
        employeePermissionRepository.deleteByShopIdAndEmployeeId(shopId, employeeId);
        if (!(permissionsObj instanceof List<?> permissions)) return;
        for (Object permission : permissions) {
            EmployeePermission entity = new EmployeePermission();
            entity.setShopId(shopId);
            entity.setEmployeeId(employeeId);
            entity.setPermKey(toStoragePermission(String.valueOf(permission)));
            entity.setEnabled(true);
            employeePermissionRepository.save(entity);
        }
    }

    private List<String> permissions(Long shopId, Long employeeId) {
        return employeePermissionRepository.findByShopIdAndEmployeeIdAndEnabledTrue(shopId, employeeId).stream()
                .map(EmployeePermission::getPermKey)
                .map(this::toResponsePermission)
                .toList();
    }

    private String toStoragePermission(String permission) {
        return switch (permission) {
            case "orders" -> "order_manage";
            case "inventory" -> "inventory_manage";
            case "finance" -> "finance_view";
            default -> permission;
        };
    }

    private String toResponsePermission(String permission) {
        return switch (permission) {
            case "order_manage" -> "orders";
            case "inventory_manage" -> "inventory";
            case "finance_view" -> "finance";
            default -> permission;
        };
    }

    private Employee find(Long shopId, Long id) {
        return employeeRepository.findByShopIdAndId(shopId, id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return "sha256$" + HexFormat.of().formatHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private String emptyToNull(String value) { return value == null || value.isBlank() ? null : value; }
    private String asString(Object value) { return value == null ? null : String.valueOf(value); }
}
