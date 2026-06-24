package com.washhelper.service;

import com.washhelper.dto.PageResponse;
import com.washhelper.entity.Customer;
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

    @Autowired
    private CustomerService customerService;

    @Cacheable(value = "orders", key = "#shopId + '_' + #status + '_' + #search + '_' + #page + '_' + #pageSize")
    public PageResponse<Order> getOrders(Long shopId, String status, String search, int page, int pageSize) {
        int normalizedPage = Math.max(page, 1);
        Pageable pageable = PageRequest.of(normalizedPage - 1, pageSize);
        Page<Order> orderPage = orderRepository.findByShopStatusAndSearch(shopId, status, search, pageable);
        return new PageResponse<>(orderPage.getContent(), normalizedPage, pageSize, orderPage.getTotalElements());
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public Order createOrder(Long shopId, Order order) {
        order.setShopId(shopId);
        if (order.getOrderId() == null || order.getOrderId().isBlank()) {
            String orderId = "ORD-" + shopId + "-" + (System.currentTimeMillis() % 100000);
            order.setOrderId(orderId);
        }
        Customer customer = null;
        if (order.getCustomerId() != null) {
            customer = customerService.resolveCustomer(shopId, String.valueOf(order.getCustomerId()));
        } else if (order.getCustomerPhone() != null && !order.getCustomerPhone().isBlank()) {
            customer = customerService.resolveCustomerByPhone(shopId, order.getCustomerPhone());
        }
        if (customer != null) {
            order.setCustomerId(customer.getId());
            order.setCustomerName(customer.getName());
            order.setCustomerPhone(customer.getPhone());
            order.setCustomerMemberType(customer.getMemberType());
        }
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("pending");
        }
        return orderRepository.save(order);
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public void updateOrderStatus(Long shopId, Long id, String status) {
        Order order = find(shopId, id);
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public void deleteOrder(Long shopId, Long id) {
        Order order = find(shopId, id);
        orderRepository.delete(order);
    }

    public void printOrder(Long shopId, Long id) {
        find(shopId, id);
    }

    public java.util.Map<String, Object> stats(Long shopId, String range) {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime start;
        java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();
        String normalized = range == null ? "today" : range.toLowerCase();
        switch (normalized) {
            case "week":
                start = today.with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                break;
            case "month":
                start = today.withDayOfMonth(1).atStartOfDay();
                break;
            case "today":
            default:
                start = today.atStartOfDay();
                normalized = "today";
                break;
        }

        long orderCount = orderRepository.countByShopIdAndCreatedAtBetween(shopId, start, end);
        long pending = orderRepository.countByShopIdAndStatusIn(shopId,
                java.util.Arrays.asList("pending", "washing", "pickup"));
        java.math.BigDecimal revenue = orderRepository.sumRevenueBetween(shopId, start, end);
        if (revenue == null) revenue = java.math.BigDecimal.ZERO;

        java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("range", normalized);
        data.put("orderCount", orderCount);
        data.put("pendingOrders", pending);
        data.put("revenue", revenue);
        return data;
    }
    private Order find(Long shopId, Long id) {
        return orderRepository.findByShopIdAndId(shopId, id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
