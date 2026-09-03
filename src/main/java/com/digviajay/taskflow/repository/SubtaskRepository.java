package com.digviajay.taskflow.repository;

import com.digviajay.taskflow.entity.Subtask;
import com.digviajay.taskflow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
    List<Subtask> findByTask(Task task);
    List<Subtask> findByTaskOrderByCreatedAtAsc(Task task);
}