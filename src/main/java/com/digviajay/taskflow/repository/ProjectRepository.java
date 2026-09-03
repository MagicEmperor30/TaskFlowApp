package com.digviajay.taskflow.repository;

import com.digviajay.taskflow.entity.Project;
import com.digviajay.taskflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUser(User user);
    List<Project> findByUserOrderByCreatedAtDesc(User user);
}