package com.washhelper.controller;

import com.washhelper.dto.ApiResponse;
import com.washhelper.dto.PageResponse;
import com.washhelper.service.CustomerService;
import com.washhelper.service.WalletService;
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

    @PostMapping({
            "/customers/{id}/wallet/recharge",
            "/customers/{id}/recharge"
    })
    public ApiResponse<Map<String, Object>> recharge(@PathVariable String id,
                                                     @RequestBody Map<String, Object> request) {
        return ApiResponse.success(walletService.recharge(id, request));
    }

    @PostMapping({
            "/member-wallet/recharge",
            "/wallet/recharge"
    })
    public ApiResponse<Map<String, Object>> rechargeWithoutPath(@RequestBody Map<String, Object> request) {
        return ApiResponse.success(walletService.recharge(null, request));
    }

    @PostMapping({
            "/customers/{id}/wallet/transactions",
            "/customers/{id}/transactions"
    })
    public ApiResponse<Map<String, Object>> createCustomerTransaction(@PathVariable String id,
                                                                     @RequestBody Map<String, Object> request) {
        return ApiResponse.success(walletService.createTransaction(id, request));
    }

    @PostMapping({
            "/wallet/transactions",
            "/transactions"
    })
    public ApiResponse<Map<String, Object>> createTransaction(@RequestBody Map<String, Object> request) {
        return ApiResponse.success(walletService.createTransaction(null, request));
    }

    @GetMapping("/customers/{id}/wallet/transactions")
    public PageResponse<Map<String, Object>> getCustomerTransactions(
            @PathVariable String id,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return walletService.getCustomerTransactions(id, type, page, pageSize, startDate, endDate);
    }

    @GetMapping("/transactions")
    public PageResponse<Map<String, Object>> getTransactions(
            @RequestParam(required = false) String customerId,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Long dbCustomerId = customerId == null || customerId.isBlank()
                ? null
                : customerService.resolveCustomer(customerId).getId();
        return walletService.searchTransactions(dbCustomerId, type, page, pageSize, null, null);
    }
}
