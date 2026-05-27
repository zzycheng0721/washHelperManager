package com.washhelper.repository;

import com.washhelper.entity.MemberWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberWalletRepository extends JpaRepository<MemberWallet, Long> {
    Optional<MemberWallet> findByCustomerId(Long customerId);
}
