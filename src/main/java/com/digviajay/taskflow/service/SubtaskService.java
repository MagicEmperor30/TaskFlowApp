package com.digviajay.taskflow.service;

import com.digviajay.taskflow.entity.Subtask;
import com.digviajay.taskflow.entity.Task;
import com.digviajay.taskflow.repository.SubtaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;

    public Subtask createSubtask(String title, Task task) {
        Subtask subtask = new Subtask();
        subtask.setTitle(title);
        subtask.setTask(task);
        return subtaskRepository.save(subtask);
    }

    public Subtask toggleComplete(Long subtaskId) {
        Subtask subtask = subtaskRepository.findById(subtaskId)
                .orElseThrow(() -> new RuntimeException("Subtask not found"));
        subtask.setCompleted(!subtask.isCompleted());
        return subtaskRepository.save(subtask);
    }

    public List<Subtask> getSubtasksByTask(Task task) {
        return subtaskRepository.findByTaskOrderByCreatedAtAsc(task);
    }

    public void deleteSubtask(Long id) {
        subtaskRepository.deleteById(id);
    }
}