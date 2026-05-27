package com.washhelper.repository;

import com.washhelper.entity.ShopHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopHourRepository extends JpaRepository<ShopHour, Long> {
    List<ShopHour> findByShopIdOrderByWeekdayAsc(Long shopId);
    Optional<ShopHour> findByShopIdAndWeekday(Long shopId, Integer weekday);
}
