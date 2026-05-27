package com.washhelper.service;

import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Customer;
import com.washhelper.entity.MemberWallet;
import com.washhelper.repository.CustomerRepository;
import com.washhelper.repository.MemberWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MemberWalletRepository memberWalletRepository;

    public PageResponse<Customer> getCustomers(Long shopId, String type, String search, int page, int pageSize) {
        int normalizedPage = Math.max(page, 1);
        Pageable pageable = PageRequest.of(normalizedPage - 1, pageSize);
        Page<Customer> customerPage = customerRepository.findByShopTypeAndSearch(shopId, type, search, pageable);
        return new PageResponse<>(customerPage.getContent(), normalizedPage, pageSize, customerPage.getTotalElements());
    }

    @Transactional
    public Customer createCustomer(Long shopId, Customer customer) {
        customer.setShopId(shopId);
        customer.setDeleted(false);
        if (customer.getCustomerId() == null || customer.getCustomerId().isBlank()) {
            String customerId = "CUST-" + shopId + "-" + String.format("%06d", (System.currentTimeMillis() % 1000000));
            customer.setCustomerId(customerId);
        }
        return customerRepository.save(customer);
    }

    public void contactCustomer(Long shopId, Long id) {
        resolveCustomer(shopId, String.valueOf(id));
    }

    public Customer resolveCustomer(Long shopId, String id) {
        if (id != null && id.matches("\\d+")) {
            return customerRepository.findByShopIdAndIdAndDeletedFalse(shopId, Long.parseLong(id))
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }
        return customerRepository.findByShopIdAndCustomerIdAndDeletedFalse(shopId, id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer resolveCustomerByPhone(Long shopId, String phone) {
        if (phone == null || phone.isBlank()) {
            throw new RuntimeException("Customer phone is required");
        }
        return customerRepository.findFirstByShopIdAndPhoneAndDeletedFalse(shopId, phone)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Map<String, Object> getCustomerDetail(Long shopId, String id) {
        return toCustomerDetail(resolveCustomer(shopId, id));
    }

    @Transactional
    public Map<String, Object> updateCustomer(Long shopId, String id, Map<String, Object> request) {
        Customer customer = resolveCustomer(shopId, id);
        applyCustomerFields(customer, request);
        Customer saved = customerRepository.save(customer);
        if (request.containsKey("balance")) {
            updateBalance(shopId, saved.getId().toString(), toBigDecimal(request.get("balance")));
        }
        return toCustomerDetail(saved);
    }

    @Transactional
    public void deleteCustomer(Long shopId, String id) {
        Customer customer = resolveCustomer(shopId, id);
        customer.setDeleted(true);
        customerRepository.save(customer);
    }

    @Transactional
    public Map<String, Object> updateBalance(Long shopId, String id, BigDecimal balance) {
        Customer customer = resolveCustomer(shopId, id);
        MemberWallet wallet = memberWalletRepository.findByShopIdAndCustomerId(shopId, customer.getId())
                .orElseGet(() -> {
                    MemberWallet created = new MemberWallet();
                    created.setShopId(shopId);
                    created.setCustomerId(customer.getId());
                    return created;
                });
        wallet.setShopId(shopId);
        wallet.setBalance(balance == null ? BigDecimal.ZERO : balance);
        MemberWallet saved = memberWalletRepository.save(wallet);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("customerId", customer.getCustomerId());
        data.put("balance", saved.getBalance());
        return data;
    }

    public Map<String, Object> toCustomerDetail(Customer customer) {
        BigDecimal balance = memberWalletRepository.findByShopIdAndCustomerId(customer.getShopId(), customer.getId())
                .map(MemberWallet::getBalance)
                .orElse(BigDecimal.ZERO);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", customer.getId());
        data.put("shopId", customer.getShopId());
        data.put("customerId", customer.getCustomerId());
        data.put("name", customer.getName());
        data.put("phone", customer.getPhone());
        data.put("memberType", customer.getMemberType());
        data.put("notes", customer.getNotes());
        data.put("balance", balance);
        data.put("avatarUrl", customer.getAvatarUrl());
        data.put("createdAt", customer.getCreatedAt());
        data.put("updatedAt", customer.getUpdatedAt());
        return data;
    }

    private void applyCustomerFields(Customer customer, Map<String, Object> request) {
        if (request.containsKey("name")) customer.setName(asString(request.get("name")));
        if (request.containsKey("phone")) customer.setPhone(asString(request.get("phone")));
        if (request.containsKey("memberType")) customer.setMemberType(asString(request.get("memberType")));
        if (request.containsKey("notes")) customer.setNotes(asString(request.get("notes")));
        Object avatar = request.containsKey("avatarUrl") ? request.get("avatarUrl") : request.get("avatar");
        if (avatar != null) customer.setAvatarUrl(asString(avatar));
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        return new BigDecimal(String.valueOf(value));
    }
}
