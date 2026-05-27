package com.washhelper.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId = 1L;

    @Column(name = "order_id", length = 50)
    private String orderId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(length = 100)
    private String customerName;

    @Column(length = 20)
    private String customerPhone;

    @Column(length = 50)
    private String customerMemberType;

    @Column(length = 50)
    private String status;

    @Column(length = 100)
    private String service;

    @Column(length = 50)
    private String serviceType;

    @Column(columnDefinition = "TEXT")
    private String specialReqs;

    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "pending_at")
    private LocalDateTime pendingAt;

    @Column(name = "washing_at")
    private LocalDateTime washingAt;

    @Column(name = "pickup_at")
    private LocalDateTime pickupAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (shopId == null) {
            shopId = 1L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
