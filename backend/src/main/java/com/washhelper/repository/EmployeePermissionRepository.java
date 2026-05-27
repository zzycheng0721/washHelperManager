package com.washhelper.repository;

import com.washhelper.entity.EmployeePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeePermissionRepository extends JpaRepository<EmployeePermission, Long> {
    List<EmployeePermission> findByEmployeeIdAndEnabledTrue(Long employeeId);
    List<EmployeePermission> findByEmployeeId(Long employeeId);
    void deleteByEmployeeId(Long employeeId);
}
