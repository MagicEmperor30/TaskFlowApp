package com.digviajay.taskflow.controller;

import com.digviajay.taskflow.entity.Project;
import com.digviajay.taskflow.entity.Task;
import com.digviajay.taskflow.entity.User;
import com.digviajay.taskflow.service.ProjectService;
import com.digviajay.taskflow.service.TaskService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping("/")
    public String root(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        // If user is logged in, redirect to dashboard
        if (loggedInUser != null) {
            return "redirect:/dashboard";
        }
        // Otherwise show landing page
        return "landingpage";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        List<Project> projects = projectService.getProjectsForUser(loggedInUser);
        List<Task> myTasks = taskService.getTasksAssignedToUser(loggedInUser);

        // stats
        long completedTasks = myTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.DONE).count();
        long pendingTasks = myTasks.size() - completedTasks;

        // fetch all tasks for all projects in ONE query instead of one per project
        List<Task> allProjectTasks = taskService.getTasksByProjects(projects);

        Map<Long, Integer> projectProgressMap = new HashMap<>();
        for (Project project : projects) {
            List<Task> tasks = allProjectTasks.stream()
                    .filter(t -> t.getProject().getId().equals(project.getId()))
                    .toList();

            if (tasks.isEmpty()) {
                projectProgressMap.put(project.getId(), 0);
            } else {
                long done = tasks.stream()
                        .filter(t -> t.getStatus() == Task.TaskStatus.DONE).count();
                int percent = (int) ((done * 100) / tasks.size());
                projectProgressMap.put(project.getId(), percent);
            }
        }

        model.addAttribute("projects", projects);
        model.addAttribute("myTasks", myTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("projectProgressMap", projectProgressMap);
        model.addAttribute("user", loggedInUser);
        return "dashboard";
    }
}