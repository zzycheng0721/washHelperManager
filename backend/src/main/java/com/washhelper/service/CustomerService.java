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

    public PageResponse<Customer> getCustomers(String type, String search, int page, int pageSize) {
        int normalizedPage = Math.max(page, 1);
        Pageable pageable = PageRequest.of(normalizedPage - 1, pageSize);
        Page<Customer> customerPage = customerRepository.findByTypeAndSearch(type, search, pageable);
        return new PageResponse<>(
                customerPage.getContent(),
                normalizedPage,
                pageSize,
                customerPage.getTotalElements()
        );
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customer.getCustomerId() == null || customer.getCustomerId().isBlank()) {
            String customerId = "CUST-" + String.format("%06d", (System.currentTimeMillis() % 1000000));
            customer.setCustomerId(customerId);
        }
        return customerRepository.save(customer);
    }

    public void contactCustomer(Long id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer resolveCustomer(String id) {
        if (id != null && id.matches("\\d+")) {
            return customerRepository.findById(Long.parseLong(id))
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }
        return customerRepository.findByCustomerId(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Map<String, Object> getCustomerDetail(String id) {
        return toCustomerDetail(resolveCustomer(id));
    }

    @Transactional
    public Map<String, Object> updateCustomer(String id, Map<String, Object> request) {
        Customer customer = resolveCustomer(id);
        applyCustomerFields(customer, request);
        Customer saved = customerRepository.save(customer);
        if (request.containsKey("balance")) {
            updateBalance(saved.getId().toString(), toBigDecimal(request.get("balance")));
        }
        return toCustomerDetail(saved);
    }

    @Transactional
    public void deleteCustomer(String id) {
        Customer customer = resolveCustomer(id);
        customerRepository.delete(customer);
    }

    @Transactional
    public Map<String, Object> updateBalance(String id, BigDecimal balance) {
        Customer customer = resolveCustomer(id);
        MemberWallet wallet = memberWalletRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    MemberWallet created = new MemberWallet();
                    created.setCustomerId(customer.getId());
                    return created;
                });
        wallet.setBalance(balance == null ? BigDecimal.ZERO : balance);
        MemberWallet saved = memberWalletRepository.save(wallet);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("customerId", customer.getCustomerId());
        data.put("balance", saved.getBalance());
        return data;
    }

    public Map<String, Object> toCustomerDetail(Customer customer) {
        BigDecimal balance = memberWalletRepository.findByCustomerId(customer.getId())
                .map(MemberWallet::getBalance)
                .orElse(BigDecimal.ZERO);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", customer.getId());
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
        if (request.containsKey("name")) {
            customer.setName(asString(request.get("name")));
        }
        if (request.containsKey("phone")) {
            customer.setPhone(asString(request.get("phone")));
        }
        if (request.containsKey("memberType")) {
            customer.setMemberType(asString(request.get("memberType")));
        }
        if (request.containsKey("notes")) {
            customer.setNotes(asString(request.get("notes")));
        }
        Object avatar = request.containsKey("avatarUrl") ? request.get("avatarUrl") : request.get("avatar");
        if (avatar != null) {
            customer.setAvatarUrl(asString(avatar));
        }
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
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
}
