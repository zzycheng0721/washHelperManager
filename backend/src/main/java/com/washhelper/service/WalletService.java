package com.washhelper.service;

import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Customer;
import com.washhelper.entity.MemberTransaction;
import com.washhelper.entity.MemberWallet;
import com.washhelper.repository.MemberTransactionRepository;
import com.washhelper.repository.MemberWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private MemberWalletRepository memberWalletRepository;

    @Autowired
    private MemberTransactionRepository memberTransactionRepository;

    public List<Map<String, Object>> getRechargePackages() {
        return List.of(
                packageMap("pkg-100", 100, 10, true),
                packageMap("pkg-500", 500, 60, false),
                packageMap("pkg-1000", 1000, 150, false),
                packageMap("pkg-2000", 2000, 360, false)
        );
    }

    @Transactional
    public Map<String, Object> recharge(String pathCustomerId, Map<String, Object> request) {
        Customer customer = resolveCustomer(pathCustomerId, request);
        BigDecimal amount = toBigDecimal(request.get("amount"));
        BigDecimal giftAmount = toBigDecimal(request.getOrDefault("giftAmount", 0));
        BigDecimal changeAmount = amount.add(giftAmount);

        MemberWallet wallet = getOrCreateWallet(customer.getId());
        BigDecimal balance = wallet.getBalance().add(changeAmount);
        wallet.setBalance(balance);
        wallet.setLastRechargeAt(LocalDateTime.now());
        memberWalletRepository.save(wallet);

        MemberTransaction tx = new MemberTransaction();
        tx.setCustomerId(customer.getId());
        tx.setType("recharge");
        tx.setAmount(changeAmount);
        tx.setBalanceAfter(balance);
        tx.setPaymentMethod(asString(request.getOrDefault("channel", request.get("paymentMethod"))));
        tx.setRemark(asString(request.getOrDefault("remark", "member recharge")));
        MemberTransaction saved = memberTransactionRepository.save(tx);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("transactionId", txId(saved.getId()));
        data.put("customerId", customer.getCustomerId());
        data.put("rechargeAmount", amount);
        data.put("giftAmount", giftAmount);
        data.put("changeAmount", changeAmount);
        data.put("balance", balance);
        data.put("createdAt", saved.getCreatedAt());
        return data;
    }

    @Transactional
    public Map<String, Object> createTransaction(String pathCustomerId, Map<String, Object> request) {
        Customer customer = resolveCustomer(pathCustomerId, request);
        String type = normalizeType(asString(request.getOrDefault("type", "adjust")));
        String direction = asString(request.get("direction"));
        BigDecimal amount = toBigDecimal(request.get("amount"));
        BigDecimal signedAmount = isOut(direction, type) ? amount.abs().negate() : amount.abs();

        MemberWallet wallet = getOrCreateWallet(customer.getId());
        BigDecimal balance = wallet.getBalance().add(signedAmount);
        wallet.setBalance(balance);
        memberWalletRepository.save(wallet);

        MemberTransaction tx = new MemberTransaction();
        tx.setCustomerId(customer.getId());
        tx.setType(type);
        tx.setAmount(signedAmount);
        tx.setBalanceAfter(balance);
        tx.setPaymentMethod(asString(request.getOrDefault("channel", request.get("paymentMethod"))));
        tx.setOrderId(toLong(request.get("relatedOrderId")));
        tx.setRemark(asString(request.get("remark")));
        MemberTransaction saved = memberTransactionRepository.save(tx);
        return transactionMap(saved, customer);
    }

    public PageResponse<Map<String, Object>> getCustomerTransactions(String id, String type, int page, int pageSize,
                                                                     String startDate, String endDate) {
        Customer customer = customerService.resolveCustomer(id);
        return searchTransactions(customer.getId(), type, page, pageSize, startDate, endDate);
    }

    public PageResponse<Map<String, Object>> searchTransactions(Long customerId, String type, int page, int pageSize,
                                                                String startDate, String endDate) {
        int normalizedPage = Math.max(page, 1);
        Pageable pageable = PageRequest.of(normalizedPage - 1, pageSize);
        Page<MemberTransaction> txPage = memberTransactionRepository.search(
                customerId,
                normalizeQueryType(type),
                parseStart(startDate),
                parseEnd(endDate),
                pageable
        );
        List<Map<String, Object>> items = txPage.getContent().stream()
                .map(tx -> transactionMap(tx, null))
                .toList();
        return new PageResponse<>(items, normalizedPage, pageSize, txPage.getTotalElements());
    }

    private Customer resolveCustomer(String pathCustomerId, Map<String, Object> request) {
        if (pathCustomerId != null && !pathCustomerId.isBlank()) {
            return customerService.resolveCustomer(pathCustomerId);
        }
        Object bodyCustomerId = request.get("customerId");
        if (bodyCustomerId == null) {
            throw new RuntimeException("customerId is required");
        }
        return customerService.resolveCustomer(String.valueOf(bodyCustomerId));
    }

    private MemberWallet getOrCreateWallet(Long customerId) {
        return memberWalletRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    MemberWallet wallet = new MemberWallet();
                    wallet.setCustomerId(customerId);
                    wallet.setBalance(BigDecimal.ZERO);
                    return wallet;
                });
    }

    private Map<String, Object> transactionMap(MemberTransaction tx, Customer knownCustomer) {
        Customer customer = knownCustomer;
        if (customer == null) {
            try {
                customer = customerService.resolveCustomer(String.valueOf(tx.getCustomerId()));
            } catch (RuntimeException ignored) {
                customer = null;
            }
        }
        BigDecimal amount = tx.getAmount();
        String direction = amount.signum() < 0 ? "out" : "in";
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", txId(tx.getId()));
        data.put("customerId", customer == null ? String.valueOf(tx.getCustomerId()) : customer.getCustomerId());
        data.put("title", transactionTitle(tx.getType()));
        data.put("amount", amount.abs());
        data.put("direction", direction);
        data.put("balance", tx.getBalanceAfter());
        data.put("type", displayType(tx.getType()));
        data.put("channel", tx.getPaymentMethod());
        data.put("payType", paymentName(tx.getPaymentMethod()));
        data.put("relatedOrderId", tx.getOrderId());
        data.put("remark", tx.getRemark());
        data.put("createdAt", tx.getCreatedAt());
        return data;
    }

    private Map<String, Object> packageMap(String id, int amount, int gift, boolean recommended) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("amount", amount);
        data.put("gift", gift);
        data.put("recommended", recommended);
        data.put("enabled", true);
        return data;
    }

    private String txId(Long id) {
        return "TX-" + String.format("%012d", id == null ? 0 : id);
    }

    private String transactionTitle(String type) {
        if ("recharge".equals(type)) {
            return "member recharge";
        }
        if ("consume".equals(type) || "spend".equals(type)) {
            return "order consume";
        }
        return "balance adjust";
    }

    private String displayType(String type) {
        return "consume".equals(type) ? "spend" : type;
    }

    private String normalizeType(String type) {
        return "spend".equals(type) ? "consume" : (type == null ? "adjust" : type);
    }

    private String normalizeQueryType(String type) {
        if (type == null || type.isBlank()) {
            return "all";
        }
        return type;
    }

    private boolean isOut(String direction, String type) {
        return "out".equalsIgnoreCase(direction) || "spend".equals(type) || "consume".equals(type);
    }

    private String paymentName(String paymentMethod) {
        if ("wechat".equals(paymentMethod)) {
            return "wechat";
        }
        if ("alipay".equals(paymentMethod)) {
            return "alipay";
        }
        if ("cash".equals(paymentMethod)) {
            return "cash";
        }
        return paymentMethod;
    }

    private LocalDateTime parseStart(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        return LocalDate.parse(date.substring(0, 10)).atStartOfDay();
    }

    private LocalDateTime parseEnd(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        return LocalDate.parse(date.substring(0, 10)).plusDays(1).atStartOfDay().minusNanos(1);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(String.valueOf(value));
    }

    private Long toLong(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
