package com.digviajay.taskflow.repository;

import com.digviajay.taskflow.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Long> {
    Optional<Company> findByName(String name);
    Optional<Company> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
