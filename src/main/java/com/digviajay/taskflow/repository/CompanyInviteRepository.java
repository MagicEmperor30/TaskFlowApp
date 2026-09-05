package com.digviajay.taskflow.repository;

import com.digviajay.taskflow.entity.Company;
import com.digviajay.taskflow.entity.CompanyInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyInviteRepository extends JpaRepository<CompanyInvite,Long> {
    Optional<CompanyInvite> findByToken(String token);
    List<CompanyInvite> findByCompanyAndUsedFalse(Company company);
}
