package com.washhelper.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {
    
    public Map<String, Object> getAIMockResponse() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        data.put("summary", "这是一个本地 Mock 的 AI 响应，用于前端联调。");
        data.put("suggestion", "请在后端就绪后替换为真实模型输出。");
        data.put("timestamp", LocalDateTime.now().toString());
        
        response.put("ok", true);
        response.put("data", data);
        
        return response;
    }
}