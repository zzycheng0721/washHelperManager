package com.washhelper.service;

import com.washhelper.entity.Shop;
import com.washhelper.entity.ShopHour;
import com.washhelper.repository.ShopHourRepository;
import com.washhelper.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopHourRepository shopHourRepository;

    public Map<String, Object> getShopInfo(Long shopId) {
        return shopMap(getOrCreateShop(shopId));
    }

    @Transactional
    public Map<String, Object> updateShopInfo(Long shopId, Map<String, Object> request) {
        Shop shop = getOrCreateShop(shopId);
        if (request.containsKey("shopName")) shop.setName(asString(request.get("shopName")));
        if (request.containsKey("name")) shop.setName(asString(request.get("name")));
        if (request.containsKey("phone")) shop.setPhone(asString(request.get("phone")));
        if (request.containsKey("address")) shop.setAddress(asString(request.get("address")));
        if (request.containsKey("logoUrl")) shop.setLogoUrl(asString(request.get("logoUrl")));
        if (request.containsKey("latitude")) shop.setLatitude(toBigDecimal(request.get("latitude")));
        if (request.containsKey("longitude")) shop.setLongitude(toBigDecimal(request.get("longitude")));
        return shopMap(shopRepository.save(shop));
    }

    public Map<String, Object> getOperatingHours(Long shopId) {
        Shop shop = getOrCreateShop(shopId);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("paused", Boolean.TRUE.equals(shop.getPaused()));
        data.put("week", weekList(shop.getId()));
        return data;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public void updateOperatingHours(Long shopId, Map<String, Object> request) {
        Shop shop = getOrCreateShop(shopId);
        if (request.containsKey("paused")) {
            shop.setPaused(toBoolean(request.get("paused")));
            shopRepository.save(shop);
        }
        Object week = request.get("week");
        if (week instanceof List<?> days) {
            int index = 1;
            for (Object dayObj : days) {
                if (!(dayObj instanceof Map<?, ?> rawDay)) continue;
                Map<String, Object> day = (Map<String, Object>) rawDay;
                Integer weekday = day.containsKey("weekday") ? toInteger(day.get("weekday")) : dayIndex(day.get("day"), index);
                ShopHour hour = shopHourRepository.findByShopIdAndWeekday(shop.getId(), weekday)
                        .orElseGet(() -> {
                            ShopHour created = new ShopHour();
                            created.setShopId(shop.getId());
                            created.setWeekday(weekday);
                            return created;
                        });
                hour.setOpenTime(parseTime(asString(day.getOrDefault("start", "09:00"))));
                hour.setCloseTime(parseTime(asString(day.getOrDefault("end", "21:00"))));
                hour.setOpen(toBoolean(day.getOrDefault("enabled", true)));
                shopHourRepository.save(hour);
                index++;
            }
        }
    }

    private Shop getOrCreateShop(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseGet(() -> {
                    Shop shop = new Shop();
                    shop.setId(shopId);
                    shop.setName("WashHelper");
                    shop.setPaused(false);
                    return shopRepository.save(shop);
                });
    }

    private List<Map<String, Object>> weekList(Long shopId) {
        List<ShopHour> hours = shopHourRepository.findByShopIdOrderByWeekdayAsc(shopId);
        if (hours.isEmpty()) {
            for (int i = 1; i <= 7; i++) {
                ShopHour hour = new ShopHour();
                hour.setShopId(shopId);
                hour.setWeekday(i);
                hour.setOpenTime(LocalTime.of(i >= 6 ? 10 : 9, 0));
                hour.setCloseTime(LocalTime.of(i >= 6 ? 22 : 21, 0));
                hour.setOpen(true);
                shopHourRepository.save(hour);
            }
            hours = shopHourRepository.findByShopIdOrderByWeekdayAsc(shopId);
        }
        return hours.stream().map(this::hourMap).toList();
    }

    private Map<String, Object> shopMap(Shop shop) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", shop.getId());
        data.put("shopId", shop.getId());
        data.put("shopName", shop.getName());
        data.put("name", shop.getName());
        data.put("phone", shop.getPhone());
        data.put("address", shop.getAddress());
        data.put("logoUrl", shop.getLogoUrl());
        data.put("latitude", shop.getLatitude());
        data.put("longitude", shop.getLongitude());
        data.put("updatedAt", shop.getUpdatedAt());
        return data;
    }

    private Map<String, Object> hourMap(ShopHour hour) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("day", dayName(hour.getWeekday()));
        data.put("weekday", hour.getWeekday());
        data.put("start", formatTime(hour.getOpenTime()));
        data.put("end", formatTime(hour.getCloseTime()));
        data.put("enabled", Boolean.TRUE.equals(hour.getOpen()));
        return data;
    }

    private String dayName(Integer weekday) {
        String[] days = {
                "\u5468\u4e00",
                "\u5468\u4e8c",
                "\u5468\u4e09",
                "\u5468\u56db",
                "\u5468\u4e94",
                "\u5468\u516d",
                "\u5468\u65e5"
        };
        int idx = weekday == null ? 0 : Math.max(1, Math.min(7, weekday)) - 1;
        return days[idx];
    }

    private Integer dayIndex(Object value, int fallback) {
        if (value == null) return fallback;
        String day = String.valueOf(value).toLowerCase();
        if (day.contains("1") || day.contains("mon") || day.contains("\u5468\u4e00") || day.equals("\u4e00")) return 1;
        if (day.contains("2") || day.contains("tue") || day.contains("\u5468\u4e8c") || day.equals("\u4e8c")) return 2;
        if (day.contains("3") || day.contains("wed") || day.contains("\u5468\u4e09") || day.equals("\u4e09")) return 3;
        if (day.contains("4") || day.contains("thu") || day.contains("\u5468\u56db") || day.equals("\u56db")) return 4;
        if (day.contains("5") || day.contains("fri") || day.contains("\u5468\u4e94") || day.equals("\u4e94")) return 5;
        if (day.contains("6") || day.contains("sat") || day.contains("\u5468\u516d") || day.equals("\u516d")) return 6;
        if (day.contains("7") || day.contains("sun") || day.contains("\u5468\u65e5") || day.contains("\u5468\u5929") || day.equals("\u65e5") || day.equals("\u5929")) return 7;
        return fallback;
    }

    private LocalTime parseTime(String value) { return value.length() == 5 ? LocalTime.parse(value + ":00") : LocalTime.parse(value); }
    private String formatTime(LocalTime time) { return time == null ? null : time.toString().substring(0, 5); }
    private String asString(Object value) { return value == null ? null : String.valueOf(value); }
    private Boolean toBoolean(Object value) { return value instanceof Boolean bool ? bool : Boolean.parseBoolean(String.valueOf(value)); }
    private Integer toInteger(Object value) { return value instanceof Number number ? number.intValue() : Integer.valueOf(String.valueOf(value)); }
    private BigDecimal toBigDecimal(Object value) {
        if (value == null || String.valueOf(value).isBlank()) return null;
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        return new BigDecimal(String.valueOf(value));
    }
}
