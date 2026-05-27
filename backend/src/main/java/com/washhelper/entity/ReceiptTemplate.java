package com.washhelper.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "receipt_templates")
public class ReceiptTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "header_text", length = 100)
    private String headerText;

    @Column(name = "footer_text", length = 255)
    private String footerText;

    @Column(name = "show_logo", nullable = false)
    private Boolean showLogo = true;

    @Column(name = "show_customer_name", nullable = false)
    private Boolean showCustomerName = true;

    @Column(name = "show_wash_instructions", nullable = false)
    private Boolean showWashInstructions = false;

    @Column(name = "paper_width", nullable = false, length = 10)
    private String paperWidth = "58mm";

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        updatedAt = LocalDateTime.now();
        if (showLogo == null) {
            showLogo = true;
        }
        if (showCustomerName == null) {
            showCustomerName = true;
        }
        if (showWashInstructions == null) {
            showWashInstructions = false;
        }
        if (paperWidth == null) {
            paperWidth = "58mm";
        }
    }
}
