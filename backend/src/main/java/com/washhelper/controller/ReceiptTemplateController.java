package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.service.ReceiptTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/receipt-template")
public class ReceiptTemplateController {
    @Autowired
    private ReceiptTemplateService receiptTemplateService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getTemplate() {
        return ApiResponse.success(receiptTemplateService.getTemplate());
    }

    @PutMapping
    public ApiResponse<Void> updateTemplate(@RequestBody Map<String, Object> request) {
        receiptTemplateService.updateTemplate(request);
        return ApiResponse.success();
    }
}
