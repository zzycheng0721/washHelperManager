package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.service.ReceiptTemplateService;
import com.washhelper.util.TenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/receipt-template")
public class ReceiptTemplateController {
    @Autowired
    private ReceiptTemplateService receiptTemplateService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getTemplate(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader) {
        return ApiResponse.success(receiptTemplateService.getTemplate(TenantUtil.resolve(shopId, shopIdHeader)));
    }

    @PutMapping
    public ApiResponse<Void> updateTemplate(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestBody Map<String, Object> request) {
        receiptTemplateService.updateTemplate(TenantUtil.resolve(shopId, shopIdHeader), request);
        return ApiResponse.success();
    }
}
