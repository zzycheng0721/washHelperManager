package com.washhelper.repository;

import com.washhelper.entity.ReceiptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiptTemplateRepository extends JpaRepository<ReceiptTemplate, Long> {
    Optional<ReceiptTemplate> findFirstByOrderByIdAsc();
}
