package com.washhelper.service;

import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Customer;
import com.washhelper.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public PageResponse<Customer> getCustomers(String type, String search, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Customer> customerPage = customerRepository.findByTypeAndSearch(type, search, pageable);
        
        return new PageResponse<>(
            customerPage.getContent(),
            page,
            pageSize,
            customerPage.getTotalElements()
        );
    }
    
    @Transactional
    public Customer createCustomer(Customer customer) {
        String customerId = "CUST-" + String.format("%03d", (System.currentTimeMillis() % 1000));
        customer.setCustomerId(customerId);
        return customerRepository.save(customer);
    }
    
    public void contactCustomer(Long id) {
        customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}