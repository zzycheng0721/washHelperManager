package com.washhelper.repository;

import com.washhelper.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByShopIdAndId(Long shopId, Long id);

    @Query("SELECT o FROM Order o WHERE o.shopId = :shopId AND " +
           "(:status IS NULL OR :status = '' OR :status = 'all' OR o.status = :status) AND " +
           "(:search IS NULL OR :search = '' OR o.orderId LIKE %:search% OR o.customerName LIKE %:search% OR o.customerPhone LIKE %:search%)")
    Page<Order> findByShopStatusAndSearch(@Param("shopId") Long shopId,
                                          @Param("status") String status,
                                          @Param("search") String search,
                                          Pageable pageable);

    long countByShopIdAndCreatedAtBetween(Long shopId, LocalDateTime start, LocalDateTime end);

    long countByShopIdAndStatusIn(Long shopId, List<String> statuses);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.shopId = :shopId " +
           "AND o.status <> 'cancelled' " +
           "AND o.createdAt >= :start AND o.createdAt < :end")
    java.math.BigDecimal sumRevenueBetween(@Param("shopId") Long shopId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}