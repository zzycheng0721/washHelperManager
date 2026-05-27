package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.service.CustomerService;
import com.washhelper.service.WalletService;
import com.washhelper.util.TenantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/recharge/packages")
    public Map<String, Object> getRechargePackages() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", walletService.getRechargePackages());
        return response;
    }

    @PostMapping({"/customers/{id}/wallet/recharge", "/customers/{id}/recharge"})
    public ApiResponse<Map<String, Object>> recharge(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        return ApiResponse.success(walletService.recharge(TenantUtil.resolve(shopId, shopIdHeader), id, request));
    }

    @PostMapping({"/member-wallet/recharge", "/wallet/recharge"})
    public ApiResponse<Map<String, Object>> rechargeWithoutPath(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestBody Map<String, Object> request) {
        return ApiResponse.success(walletService.recharge(TenantUtil.resolve(shopId, shopIdHeader), null, request));
    }

    @PostMapping({"/customers/{id}/wallet/transactions", "/customers/{id}/transactions"})
    public ApiResponse<Map<String, Object>> createCustomerTransaction(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        return ApiResponse.success(walletService.createTransaction(TenantUtil.resolve(shopId, shopIdHeader), id, request));
    }

    @PostMapping({"/wallet/transactions", "/transactions"})
    public ApiResponse<Map<String, Object>> createTransaction(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestBody Map<String, Object> request) {
        return ApiResponse.success(walletService.createTransaction(TenantUtil.resolve(shopId, shopIdHeader), null, request));
    }

    @GetMapping("/customers/{id}/wallet/transactions")
    public PageResponse<Map<String, Object>> getCustomerTransactions(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @PathVariable String id,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return walletService.getCustomerTransactions(TenantUtil.resolve(shopId, shopIdHeader), id, type, page, pageSize, startDate, endDate);
    }

    @GetMapping("/transactions")
    public PageResponse<Map<String, Object>> getTransactions(
            @RequestParam(required = false) Long shopId,
            @RequestHeader(value = "X-Shop-Id", required = false) String shopIdHeader,
            @RequestParam(required = false) String customerId,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Long resolvedShopId = TenantUtil.resolve(shopId, shopIdHeader);
        Long dbCustomerId = customerId == null || customerId.isBlank()
                ? null
                : customerService.resolveCustomer(resolvedShopId, customerId).getId();
        return walletService.searchTransactions(resolvedShopId, dbCustomerId, type, page, pageSize, null, null);
    }
}
