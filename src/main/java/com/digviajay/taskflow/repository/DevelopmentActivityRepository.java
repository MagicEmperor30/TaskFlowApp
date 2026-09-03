package com.digviajay.taskflow.repository;

import com.digviajay.taskflow.entity.DevelopmentActivity;
import com.digviajay.taskflow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DevelopmentActivityRepository extends JpaRepository<DevelopmentActivity,Long> {
    List<DevelopmentActivity> findByTaskOrderByActivityTimeDesc(Task task);

    // Find activities for a task within a date range
    List<DevelopmentActivity> findByTaskAndActivityTimeBetweenOrderByActivityTimeDesc(
            Task task, LocalDateTime start, LocalDateTime end);

    // Find by task and event type
    List<DevelopmentActivity> findByTaskAndEventType(
            Task task, DevelopmentActivity.EvenType eventType);
}
