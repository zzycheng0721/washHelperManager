package com.washhelper.entity;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
@Schema(description = "订单表")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "主键ID")
    private Long id;
    
    @Column(unique = true, length = 50)
    @Schema(description = "订单编号")
    private String orderId;
    
    @Column(length = 100)
    @Schema(description = "客户姓名")
    private String customerName;
    
    @Column(length = 20)
    @Schema(description = "客户手机号")
    private String customerPhone;
    
    @Column(length = 50)
    @Schema(description = "订单状态")
    private String status;
    
    @Column(length = 100)
    @Schema(description = "服务项目")
    private String service;
    
    @Column(length = 50)
    @Schema(description = "服务类型")
    private String serviceType;
    
    @Column(columnDefinition = "TEXT")
    @Schema(description = "特殊要求")
    private String specialReqs;
    
    @Schema(description = "数量")
    private Integer quantity;
    
    @Column(name = "created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
