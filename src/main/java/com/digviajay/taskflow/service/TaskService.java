package com.digviajay.taskflow.service;

import com.digviajay.taskflow.entity.Project;
import com.digviajay.taskflow.entity.Task;
import com.digviajay.taskflow.entity.User;
import com.digviajay.taskflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task createTask(String title, String description, Task.Priority priority,
                           LocalDate deadline, Project project, User assignedUser) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDeadline(deadline);
        task.setProject(project);
        task.setAssignedUser(assignedUser);
        return taskRepository.save(task);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public List<Task> getTasksByProjects(List<Project> projects) {
        if (projects.isEmpty()) return List.of();
        return taskRepository.findByProjectIn(projects);
    }

    public List<Task> getTasksAssignedToUser(User user) {
        return taskRepository.findByAssignedUser(user);
    }

    public Task updateStatus(Long taskId, Task.TaskStatus status) {
        Task task = findById(taskId);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public Task assignTask(Long taskId, User user) {
        Task task = findById(taskId);
        task.setAssignedUser(user);
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}