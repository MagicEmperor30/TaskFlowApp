package com.digviajay.taskflow.repository;

import com.digviajay.taskflow.entity.Project;
import com.digviajay.taskflow.entity.Task;
import com.digviajay.taskflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignedUser(User user);
    List<Task> findByProjectAndAssignedUser(Project project, User user);
    List<Task> findByProjectOrderByCreatedAtDesc(Project project);
}