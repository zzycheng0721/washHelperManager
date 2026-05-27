package com.washhelper.service;

import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Order;
import com.washhelper.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Cacheable(value = "orders", key = "#status + '_' + #search + '_' + #page + '_' + #pageSize")
    public PageResponse<Order> getOrders(String status, String search, int page, int pageSize) {
        int normalizedPage = Math.max(page, 1);
        Pageable pageable = PageRequest.of(normalizedPage - 1, pageSize);
        Page<Order> orderPage = orderRepository.findByStatusAndSearch(status, search, pageable);
        return new PageResponse<>(
                orderPage.getContent(),
                normalizedPage,
                pageSize,
                orderPage.getTotalElements()
        );
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public Order createOrder(Order order) {
        if (order.getOrderId() == null || order.getOrderId().isBlank()) {
            String orderId = "ORD-" + (System.currentTimeMillis() % 10000);
            order.setOrderId(orderId);
        }
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("pending");
        }
        return orderRepository.save(order);
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public void printOrder(Long id) {
        orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
