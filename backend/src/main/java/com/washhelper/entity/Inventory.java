package com.washhelper.entity;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory")
@Schema(description = "库存表")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "主键ID")
    private Long id;
    
    @Column(length = 100)
    @Schema(description = "物料名称")
    private String name;
    
    @Column(length = 50)
    @Schema(description = "物料分类")
    private String category;
    
    @Schema(description = "可用数量")
    private Integer available;
    
    @Schema(description = "总数量")
    private Integer total;
    
    @Column(length = 20)
    @Schema(description = "单位")
    private String unit;
    
    @Schema(description = "预警阈值")
    private Integer alertThreshold;
    
    @Column(columnDefinition = "TEXT")
    @Schema(description = "备注")
    private String notes;
    
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
