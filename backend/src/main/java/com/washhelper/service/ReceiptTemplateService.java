package com.washhelper.service;

import com.washhelper.entity.ReceiptTemplate;
import com.washhelper.repository.ReceiptTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ReceiptTemplateService {
    @Autowired
    private ReceiptTemplateRepository receiptTemplateRepository;

    public Map<String, Object> getTemplate() {
        return toMap(getOrCreate());
    }

    @Transactional
    public void updateTemplate(Map<String, Object> request) {
        ReceiptTemplate template = getOrCreate();
        if (request.containsKey("showLogo")) {
            template.setShowLogo(toBoolean(request.get("showLogo")));
        }
        if (request.containsKey("showCustomerName")) {
            template.setShowCustomerName(toBoolean(request.get("showCustomerName")));
        }
        if (request.containsKey("showWashInstructions")) {
            template.setShowWashInstructions(toBoolean(request.get("showWashInstructions")));
        }
        if (request.containsKey("footerText")) {
            template.setFooterText(asString(request.get("footerText")));
        }
        if (request.containsKey("headerText")) {
            template.setHeaderText(asString(request.get("headerText")));
        }
        if (request.containsKey("printWidth")) {
            template.setPaperWidth(asString(request.get("printWidth")));
        }
        if (request.containsKey("paperWidth")) {
            template.setPaperWidth(asString(request.get("paperWidth")));
        }
        receiptTemplateRepository.save(template);
    }

    private ReceiptTemplate getOrCreate() {
        return receiptTemplateRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    ReceiptTemplate template = new ReceiptTemplate();
                    template.setShopId(1L);
                    template.setHeaderText("WashHelper");
                    template.setFooterText("Thanks");
                    return receiptTemplateRepository.save(template);
                });
    }

    private Map<String, Object> toMap(ReceiptTemplate template) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("showLogo", template.getShowLogo());
        data.put("showCustomerName", template.getShowCustomerName());
        data.put("showWashInstructions", template.getShowWashInstructions());
        data.put("footerText", template.getFooterText());
        data.put("headerText", template.getHeaderText());
        data.put("printWidth", template.getPaperWidth());
        data.put("paperWidth", template.getPaperWidth());
        data.put("logoUrl", template.getLogoUrl());
        data.put("updatedAt", template.getUpdatedAt());
        return data;
    }

    private Boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
