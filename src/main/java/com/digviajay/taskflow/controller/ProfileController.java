package com.digviajay.taskflow.controller;

import com.digviajay.taskflow.entity.Task;
import com.digviajay.taskflow.entity.User;
import com.digviajay.taskflow.service.ProjectService;
import com.digviajay.taskflow.service.TaskService;
import com.digviajay.taskflow.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TaskService taskService;

    @GetMapping
    public String profile(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        List<Task> tasks = taskService.getTasksAssignedToUser(loggedInUser);
        long completed = tasks.stream().filter(t -> t.getStatus() == Task.TaskStatus.DONE).count();

        model.addAttribute("user", loggedInUser);
        model.addAttribute("totalProjects", projectService.getProjectsForUser(loggedInUser).size());
        model.addAttribute("totalTasks", tasks.size());
        model.addAttribute("completedTasks", completed);
        return "profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        try {
            if (!newPassword.equals(confirmPassword))
                throw new RuntimeException("New passwords do not match");

            userService.changePassword(loggedInUser, currentPassword, newPassword);
            model.addAttribute("user", loggedInUser);
            model.addAttribute("success", "Password updated successfully");
            model.addAttribute("totalProjects", projectService.getProjectsForUser(loggedInUser).size());
            model.addAttribute("totalTasks", taskService.getTasksAssignedToUser(loggedInUser).size());
            model.addAttribute("completedTasks", taskService.getTasksAssignedToUser(loggedInUser)
                    .stream().filter(t -> t.getStatus() == Task.TaskStatus.DONE).count());
        } catch (RuntimeException e) {
            model.addAttribute("user", loggedInUser);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("totalProjects", projectService.getProjectsForUser(loggedInUser).size());
            model.addAttribute("totalTasks", taskService.getTasksAssignedToUser(loggedInUser).size());
            model.addAttribute("completedTasks", 0);
        }
        return "profile";
    }
}