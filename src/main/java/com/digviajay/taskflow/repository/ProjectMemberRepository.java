package com.digviajay.taskflow.repository;

import com.digviajay.taskflow.entity.Company;
import com.digviajay.taskflow.entity.Project;
import com.digviajay.taskflow.entity.ProjectMember;
import com.digviajay.taskflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Project project);
    List<ProjectMember> findByUser(User user);
    List<ProjectMember> findByUserOrderByJoinedAtDesc(User user);
    Optional<ProjectMember> findByProjectAndUser(Project project, User user);
    boolean existsByProjectAndUser(Project project, User user);
    // V2 — needed to find project admin
    Optional<ProjectMember> findByProjectAndRole(Project project, ProjectMember.ProjectRole role);

    // V2 — needed for company-level isolation check
    List<ProjectMember> findByUserAndProject_Company(User user, Company company);
}