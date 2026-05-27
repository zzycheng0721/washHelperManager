package com.washhelper.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "employee_permissions")
public class EmployeePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId = 1L;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "perm_key", nullable = false, length = 50)
    private String permKey;

    @Column(nullable = false)
    private Boolean enabled = true;
}
