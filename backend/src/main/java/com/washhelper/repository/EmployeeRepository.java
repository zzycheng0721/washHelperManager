package com.washhelper.repository;

import com.washhelper.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE " +
           "(:keyword IS NULL OR e.name LIKE %:keyword% OR e.phone LIKE %:keyword% OR e.role LIKE %:keyword%) AND " +
           "(:role IS NULL OR e.role = :role) AND " +
           "(:status IS NULL OR e.status = :status)")
    Page<Employee> search(@Param("keyword") String keyword,
                          @Param("role") String role,
                          @Param("status") String status,
                          Pageable pageable);
}
