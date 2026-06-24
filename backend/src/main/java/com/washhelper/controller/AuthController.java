package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.entity.Employee;
import com.washhelper.entity.Shop;
import com.washhelper.repository.EmployeeRepository;
import com.washhelper.repository.ShopRepository;
import com.washhelper.service.CaptchaService;
import com.washhelper.service.EmployeeService;
import com.washhelper.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Value("${app.api-key}")
    private String apiKey;

    @GetMapping("/shops")
    public ApiResponse<List<Map<String, Object>>> publicShops() {
        List<Map<String, Object>> data = shopRepository.findAll().stream().map(this::shopBrief).toList();
        return ApiResponse.success(data);
    }

    @GetMapping("/captcha")
    public ApiResponse<Map<String, Object>> captcha() {
        return ApiResponse.success(captchaService.generate());
    }

    @PostMapping("/login")
    @Transactional
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, Object> request) {
        Long shopId = toLong(request.get("shopId"));
        String username = asString(request.get("username"));
        String password = asString(request.get("password"));
        String captchaToken = asString(request.get("captchaToken"));
        String captchaInput = asString(request.get("captcha"));

        if (shopId == null || username == null || username.isBlank() || password == null || password.isBlank()) {
            return ApiResponse.error("门店、用户名和密码不能为空");
        }

        if (!captchaService.verifyAndConsume(captchaToken, captchaInput)) {
            return errorWithCaptchaRefresh("验证码错误或已过期，请重试");
        }

        String identity = shopId + ":" + username.trim();
        long lockedSec = loginAttemptService.lockedSeconds(identity);
        if (lockedSec > 0) {
            return errorWithCaptchaRefresh("登录失败次数过多，请于 " + formatDuration(lockedSec) + " 后再试");
        }

        Employee employee = employeeRepository.search(shopId, username, "admin", "active",
                PageRequest.of(0, 1))
                .getContent().stream()
                .filter(e -> username.equals(e.getName()) || username.equals(e.getPhone()))
                .findFirst()
                .orElse(null);

        if (employee == null || !hashPassword(password).equals(employee.getPasswordHash())) {
            int failed = loginAttemptService.recordFailure(identity);
            int remaining = Math.max(0, LoginAttemptService.MAX_FAILURES - failed);
            String msg;
            if (failed >= LoginAttemptService.MAX_FAILURES) {
                msg = "登录失败次数过多，账号已临时锁定 15 分钟";
            } else if (employee == null) {
                msg = "该门店下未找到对应的店长账号（剩余尝试次数：" + remaining + "）";
            } else {
                msg = "用户名或密码错误（剩余尝试次数：" + remaining + "）";
            }
            return errorWithCaptchaRefresh(msg);
        }

        loginAttemptService.reset(identity);
        employee.setLastLoginAt(LocalDateTime.now());
        employeeRepository.save(employee);

        Shop shop = shopRepository.findById(shopId).orElse(null);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("apiKey", apiKey);
        data.put("shopId", shopId);
        data.put("shopName", shop != null ? shop.getName() : ("门店 " + shopId));
        data.put("employee", employeeService.toMap(employee));
        return ApiResponse.success(data);
    }

    @PostMapping("/change-password")
    @Transactional
    public ApiResponse<Void> changePassword(@RequestBody Map<String, Object> request) {
        Long shopId = toLong(request.get("shopId"));
        Long employeeId = toLong(request.get("employeeId"));
        String oldPassword = asString(request.get("oldPassword"));
        String newPassword = asString(request.get("newPassword"));

        if (shopId == null || employeeId == null || oldPassword == null || newPassword == null) {
            return ApiResponse.error("参数不完整");
        }
        if (newPassword.length() < 6) {
            return ApiResponse.error("新密码长度至少 6 位");
        }

        Employee employee = employeeRepository.findByShopIdAndId(shopId, employeeId).orElse(null);
        if (employee == null) {
            return ApiResponse.error("账号不存在");
        }
        if (!hashPassword(oldPassword).equals(employee.getPasswordHash())) {
            return ApiResponse.error("原密码错误");
        }
        employee.setPasswordHash(hashPassword(newPassword));
        employeeRepository.save(employee);
        return ApiResponse.success();
    }

    private ApiResponse<Map<String, Object>> errorWithCaptchaRefresh(String message) {
        ApiResponse<Map<String, Object>> response = ApiResponse.error(message);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("captcha", captchaService.generate());
        response.setData(data);
        return response;
    }

    private String formatDuration(long seconds) {
        if (seconds >= 60) {
            long minutes = seconds / 60;
            long sec = seconds % 60;
            return sec == 0 ? minutes + " 分钟" : minutes + " 分 " + sec + " 秒";
        }
        return seconds + " 秒";
    }

    private Map<String, Object> shopBrief(Shop shop) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", shop.getId());
        data.put("name", shop.getName());
        data.put("phone", shop.getPhone());
        data.put("address", shop.getAddress());
        data.put("paused", Boolean.TRUE.equals(shop.getPaused()));
        return data;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return "sha256$" + HexFormat.of().formatHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private String asString(Object value) { return value == null ? null : String.valueOf(value); }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.longValue();
        try { return Long.valueOf(String.valueOf(value)); } catch (Exception e) { return null; }
    }
}
