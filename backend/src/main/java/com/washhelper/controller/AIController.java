package com.washhelper.controller;

import com.washhelper.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI辅助", description = "AI mock 接口")
public class AIController {
    
    @Autowired
    private AIService aiService;
    
    @GetMapping
    @Operation(summary = "获取AI mock响应")
    public Map<String, Object> getAIMockResponse() {
        return aiService.getAIMockResponse();
    }
}
