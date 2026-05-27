package com.washhelper.repository;

import com.washhelper.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByShopIdAndIdAndDeletedFalse(Long shopId, Long id);
    Optional<Customer> findByShopIdAndCustomerIdAndDeletedFalse(Long shopId, String customerId);
    Optional<Customer> findFirstByShopIdAndPhoneAndDeletedFalse(Long shopId, String phone);

    @Query("SELECT c FROM Customer c WHERE c.shopId = :shopId AND c.deleted = false AND " +
           "(:type IS NULL OR :type = '' OR :type = 'all' OR :type = '全部' OR c.memberType = :type) AND " +
           "(:search IS NULL OR :search = '' OR c.name LIKE %:search% OR c.phone LIKE %:search%)")
    Page<Customer> findByShopTypeAndSearch(@Param("shopId") Long shopId,
                                           @Param("type") String type,
                                           @Param("search") String search,
                                           Pageable pageable);
}
