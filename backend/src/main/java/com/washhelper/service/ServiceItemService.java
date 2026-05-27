package com.washhelper.service;

import com.washhelper.dto.PageResponse;
import com.washhelper.entity.ServiceItem;
import com.washhelper.repository.ServiceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceItemService {
    @Autowired
    private ServiceItemRepository serviceItemRepository;

    public PageResponse<Map<String, Object>> search(Long shopId, String category, String keyword, Boolean enabled, int page, int pageSize) {
        int normalizedPage = Math.max(page, 1);
        Pageable pageable = PageRequest.of(normalizedPage - 1, pageSize);
        Page<ServiceItem> servicePage = serviceItemRepository.search(shopId, emptyToNull(category), emptyToNull(keyword), enabled, pageable);
        List<Map<String, Object>> items = servicePage.getContent().stream().map(this::toMap).toList();
        return new PageResponse<>(items, normalizedPage, pageSize, servicePage.getTotalElements());
    }

    public Map<String, Object> get(Long shopId, Long id) {
        return toMap(find(shopId, id));
    }

    @Transactional
    public ServiceItem create(Long shopId, Map<String, Object> request) {
        ServiceItem item = new ServiceItem();
        item.setShopId(shopId);
        apply(item, request);
        return serviceItemRepository.save(item);
    }

    @Transactional
    public void update(Long shopId, Long id, Map<String, Object> request) {
        ServiceItem item = find(shopId, id);
        apply(item, request);
        serviceItemRepository.save(item);
    }

    @Transactional
    public void delete(Long shopId, Long id) {
        ServiceItem item = find(shopId, id);
        item.setActive(false);
        serviceItemRepository.save(item);
    }

    public Map<String, Object> toMap(ServiceItem item) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", item.getId());
        data.put("shopId", item.getShopId());
        data.put("name", item.getName());
        data.put("category", item.getCategory());
        data.put("price", item.getPrice());
        data.put("duration", item.getEtaText());
        data.put("etaText", item.getEtaText());
        data.put("notes", item.getNotes());
        data.put("enabled", item.getActive());
        data.put("createdAt", item.getCreatedAt());
        data.put("updatedAt", item.getUpdatedAt());
        return data;
    }

    private void apply(ServiceItem item, Map<String, Object> request) {
        if (request.containsKey("name")) item.setName(asString(request.get("name")));
        if (request.containsKey("category")) item.setCategory(asString(request.get("category")));
        if (request.containsKey("price")) item.setPrice(toBigDecimal(request.get("price")));
        if (request.containsKey("duration")) item.setEtaText(asString(request.get("duration")));
        if (request.containsKey("etaText")) item.setEtaText(asString(request.get("etaText")));
        if (request.containsKey("notes")) item.setNotes(asString(request.get("notes")));
        if (request.containsKey("enabled")) item.setActive(toBoolean(request.get("enabled")));
        if (request.containsKey("active")) item.setActive(toBoolean(request.get("active")));
    }

    private ServiceItem find(Long shopId, Long id) {
        return serviceItemRepository.findByShopIdAndId(shopId, id)
                .orElseThrow(() -> new RuntimeException("Service item not found"));
    }

    private String emptyToNull(String value) { return value == null || value.isBlank() ? null : value; }
    private String asString(Object value) { return value == null ? null : String.valueOf(value); }
    private Boolean toBoolean(Object value) { return value instanceof Boolean bool ? bool : Boolean.parseBoolean(String.valueOf(value)); }
    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        return new BigDecimal(String.valueOf(value));
    }
}
