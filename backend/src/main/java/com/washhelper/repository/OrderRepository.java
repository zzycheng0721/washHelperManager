package com.washhelper.repository;

import com.washhelper.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR :status = '全部' OR o.status = :status) AND " +
           "(:search IS NULL OR o.orderId LIKE %:search% OR o.customerName LIKE %:search% OR o.customerPhone LIKE %:search%)")
    Page<Order> findByStatusAndSearch(@Param("status") String status, 
                                      @Param("search") String search, 
                                      Pageable pageable);
}