package com.digviajay.taskflow.repository;

import com.digviajay.taskflow.entity.Company;
import com.digviajay.taskflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // V2 — same-company search only, replaces the old global search
    List<User> findByCompanyAndUsernameContainingIgnoreCase(Company company, String username);
    List<User> findByCompanyAndEmailContainingIgnoreCase(Company company, String email);

    // V2 — get all members of a company
    List<User> findByCompany(Company company);

}