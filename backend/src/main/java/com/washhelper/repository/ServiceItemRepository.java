package com.washhelper.repository;

import com.washhelper.entity.ServiceItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    Optional<ServiceItem> findByShopIdAndId(Long shopId, Long id);

    @Query("SELECT s FROM ServiceItem s WHERE s.shopId = :shopId AND " +
           "(:category IS NULL OR s.category = :category) AND " +
           "(:keyword IS NULL OR s.name LIKE %:keyword%) AND " +
           "(:enabled IS NULL OR s.active = :enabled)")
    Page<ServiceItem> search(@Param("shopId") Long shopId,
                             @Param("category") String category,
                             @Param("keyword") String keyword,
                             @Param("enabled") Boolean enabled,
                             Pageable pageable);
}
