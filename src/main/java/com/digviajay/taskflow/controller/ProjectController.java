package com.digviajay.taskflow.controller;

import com.digviajay.taskflow.entity.Project;
import com.digviajay.taskflow.entity.ProjectMember;
import com.digviajay.taskflow.entity.Task;
import com.digviajay.taskflow.entity.User;
import com.digviajay.taskflow.service.ProjectService;
import com.digviajay.taskflow.service.TaskService;
import com.digviajay.taskflow.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private  final TaskService taskService;

    @GetMapping
    public String listProjects(Model model, HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Project> projects = projectService.getProjectsForUser(loggedInUser);

        Map<Long, Integer> projectProgressMap = new HashMap<>();

        for (Project project : projects) {

            List<Task> tasks = taskService.getTasksByProject(project);

            if (tasks.isEmpty()) {
                projectProgressMap.put(project.getId(), 0);
            } else {

                long done = tasks.stream()
                        .filter(t -> t.getStatus() == Task.TaskStatus.DONE)
                        .count();

                int progress = (int) ((done * 100) / tasks.size());

                projectProgressMap.put(project.getId(), progress);
            }
        }

        model.addAttribute("projects", projects);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("projectProgressMap", projectProgressMap);

        return "projects/list";
    }

    @GetMapping("/new")
    public String newProjectPage(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        return "projects/new";
    }

    @PostMapping("/new")
    public String createProject(@RequestParam String name,
                                @RequestParam String description,
                                HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        projectService.createProject(name, description, loggedInUser);
        return "redirect:/projects";
    }

    @GetMapping("/{id}")
    public String projectDetail(@PathVariable Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Project project = projectService.findById(id);
        List<ProjectMember> members = projectService.getMembersOfProject(id);
        boolean isAdmin = projectService.isAdminOfProject(loggedInUser, project);

        model.addAttribute("project", project);
        model.addAttribute("members", members);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("user", loggedInUser);
        return "projects/detail";
    }

    @PostMapping("/{id}/add-member")
    public String addMember(@PathVariable Long id,
                            @RequestParam String keyword,
                            HttpSession session,
                            Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Project project = projectService.findById(id);
        if (!projectService.isAdminOfProject(loggedInUser, project)) {
            return "redirect:/projects/" + id;
        }

        List<User> results = userService.searchUsers(keyword);
        List<ProjectMember> members = projectService.getMembersOfProject(id);

        model.addAttribute("project", project);
        model.addAttribute("members", members);
        model.addAttribute("isAdmin", true);
        model.addAttribute("searchResults", results);
        model.addAttribute("user", loggedInUser);
        return "projects/detail";
    }

    @PostMapping("/{id}/add-member/{userId}")
    public String confirmAddMember(@PathVariable Long id,
                                   @PathVariable Long userId,
                                   HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Project project = projectService.findById(id);
        if (projectService.isAdminOfProject(loggedInUser, project)) {
            User userToAdd = userService.findById(userId);
            projectService.addMemberToProject(id, userToAdd);
        }
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Project project = projectService.findById(id);
        if (projectService.isAdminOfProject(loggedInUser, project)) {
            projectService.deleteProject(id);
        }
        return "redirect:/projects";
    }

}