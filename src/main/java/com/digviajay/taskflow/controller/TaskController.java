package com.digviajay.taskflow.controller;

import com.digviajay.taskflow.entity.*;
import com.digviajay.taskflow.service.ProjectService;
import com.digviajay.taskflow.service.SubtaskService;
import com.digviajay.taskflow.service.TaskService;
import com.digviajay.taskflow.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final SubtaskService subtaskService;
    private final UserService userService;

    @GetMapping("/new")
    public String newTaskPage(@RequestParam Long projectId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Project project = projectService.findById(projectId);
        List<ProjectMember> members = projectService.getMembersOfProject(projectId);
        model.addAttribute("project", project);
        model.addAttribute("members", members);
        model.addAttribute("priorities", Task.Priority.values());
        return "tasks/new";
    }

    @PostMapping("/new")
    public String createTask(@RequestParam Long projectId,
                             @RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String priority,
                             @RequestParam(required = false) String deadline,
                             @RequestParam(required = false) Long assignedUserId,
                             HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Project project = projectService.findById(projectId);
        if (!projectService.isAdminOfProject(loggedInUser, project)) {
            return "redirect:/projects/" + projectId;
        }

        User assignedUser = assignedUserId != null ? userService.findById(assignedUserId) : null;
        LocalDate deadlineDate = (deadline != null && !deadline.isEmpty()) ? LocalDate.parse(deadline) : null;

        taskService.createTask(title, description,
                Task.Priority.valueOf(priority), deadlineDate, project, assignedUser);
        return "redirect:/projects/" + projectId;
    }

    @GetMapping("/{id}")
    public String taskDetail(@PathVariable Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Task task = taskService.findById(id);
        List<Subtask> subtasks = subtaskService.getSubtasksByTask(task);
        boolean isAdmin = projectService.isAdminOfProject(loggedInUser, task.getProject());
        long completedCount = task.getSubtasks().stream()
                .filter(Subtask::isCompleted)
                .count();
        model.addAttribute("task", task);
        model.addAttribute("subtasks", subtasks);
        model.addAttribute("statuses", Task.TaskStatus.values());
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("completedCount", completedCount);
        return "tasks/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        taskService.updateStatus(id, Task.TaskStatus.valueOf(status));
        return "redirect:/tasks/" + id;
    }

    @PostMapping("/{id}/subtasks")
    public String addSubtask(@PathVariable Long id, @RequestParam String title) {
        Task task = taskService.findById(id);
        subtaskService.createSubtask(title, task);
        return "redirect:/tasks/" + id;
    }

    @PostMapping("/subtasks/{subtaskId}/toggle")
    public String toggleSubtask(@PathVariable Long subtaskId) {
        Subtask subtask = subtaskService.toggleComplete(subtaskId);
        return "redirect:/tasks/" + subtask.getTask().getId();
    }

    @GetMapping("/my")
    public String myTasks(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        List<Task> tasks = taskService.getTasksAssignedToUser(loggedInUser);
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", loggedInUser);
        return "tasks/my";
    }
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";
        Task task = taskService.findById(id);
        Long projectId = task.getProject().getId();
        taskService.deleteTask(id);
        return "redirect:/projects/" + projectId;
    }
}