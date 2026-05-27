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

    public Map<String, Object> getShopInfo() {
        return shopMap(getOrCreateShop());
    }

    @Transactional
    public Map<String, Object> updateShopInfo(Map<String, Object> request) {
        Shop shop = getOrCreateShop();
        if (request.containsKey("shopName")) {
            shop.setName(asString(request.get("shopName")));
        }
        if (request.containsKey("name")) {
            shop.setName(asString(request.get("name")));
        }
        if (request.containsKey("phone")) {
            shop.setPhone(asString(request.get("phone")));
        }
        if (request.containsKey("address")) {
            shop.setAddress(asString(request.get("address")));
        }
        if (request.containsKey("logoUrl")) {
            shop.setLogoUrl(asString(request.get("logoUrl")));
        }
        if (request.containsKey("latitude")) {
            shop.setLatitude(toBigDecimal(request.get("latitude")));
        }
        if (request.containsKey("longitude")) {
            shop.setLongitude(toBigDecimal(request.get("longitude")));
        }
        return shopMap(shopRepository.save(shop));
    }

    public Map<String, Object> getOperatingHours() {
        Shop shop = getOrCreateShop();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("paused", Boolean.TRUE.equals(shop.getPaused()));
        data.put("week", weekList(shop.getId()));
        return data;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public void updateOperatingHours(Map<String, Object> request) {
        Shop shop = getOrCreateShop();
        if (request.containsKey("paused")) {
            shop.setPaused(toBoolean(request.get("paused")));
            shopRepository.save(shop);
        }
        Object week = request.get("week");
        if (week instanceof List<?> days) {
            int index = 1;
            for (Object dayObj : days) {
                if (!(dayObj instanceof Map<?, ?> rawDay)) {
                    continue;
                }
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

    private Shop getOrCreateShop() {
        return shopRepository.findAll().stream().findFirst()
                .orElseGet(() -> {
                    Shop shop = new Shop();
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
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        int idx = weekday == null ? 0 : Math.max(1, Math.min(7, weekday)) - 1;
        return days[idx];
    }

    private Integer dayIndex(Object value, int fallback) {
        if (value == null) {
            return fallback;
        }
        String day = String.valueOf(value);
        if (day.contains("一")) return 1;
        if (day.contains("二")) return 2;
        if (day.contains("三")) return 3;
        if (day.contains("四")) return 4;
        if (day.contains("五")) return 5;
        if (day.contains("六")) return 6;
        if (day.contains("日") || day.contains("天")) return 7;
        return fallback;
    }

    private LocalTime parseTime(String value) {
        return value.length() == 5 ? LocalTime.parse(value + ":00") : LocalTime.parse(value);
    }

    private String formatTime(LocalTime time) {
        return time == null ? null : time.toString().substring(0, 5);
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return null;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(String.valueOf(value));
    }
}
