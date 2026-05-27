package com.washhelper.entity;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customers")
@Schema(description = "客户表")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "主键ID")
    private Long id;
    
    @Column(unique = true, length = 50)
    @Schema(description = "客户编号")
    private String customerId;
    
    @Column(length = 100)
    @Schema(description = "客户姓名")
    private String name;
    
    @Column(length = 20)
    @Schema(description = "手机号")
    private String phone;
    
    @Column(length = 50)
    @Schema(description = "会员类型")
    private String memberType;
    
    @Column(columnDefinition = "TEXT")
    @Schema(description = "备注")
    private String notes;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;
    
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
