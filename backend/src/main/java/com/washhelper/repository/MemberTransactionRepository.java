package com.washhelper.repository;

import com.washhelper.entity.MemberTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MemberTransactionRepository extends JpaRepository<MemberTransaction, Long> {
    @Query("SELECT t FROM MemberTransaction t WHERE t.shopId = :shopId AND " +
           "(:customerId IS NULL OR t.customerId = :customerId) AND " +
           "(:type IS NULL OR :type = 'all' OR t.type = :type OR (:type = 'spend' AND t.type = 'consume')) AND " +
           "(:startAt IS NULL OR t.createdAt >= :startAt) AND " +
           "(:endAt IS NULL OR t.createdAt <= :endAt) " +
           "ORDER BY t.createdAt DESC")
    Page<MemberTransaction> search(@Param("shopId") Long shopId,
                                   @Param("customerId") Long customerId,
                                   @Param("type") String type,
                                   @Param("startAt") LocalDateTime startAt,
                                   @Param("endAt") LocalDateTime endAt,
                                   Pageable pageable);
}
