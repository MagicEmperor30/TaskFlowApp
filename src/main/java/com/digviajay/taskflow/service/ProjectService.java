package com.digviajay.taskflow.service;

import com.digviajay.taskflow.entity.Project;
import com.digviajay.taskflow.entity.ProjectMember;
import com.digviajay.taskflow.entity.User;
import com.digviajay.taskflow.repository.ProjectMemberRepository;
import com.digviajay.taskflow.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public Project createProject(String name, String description, User creator) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setUser(creator);
        Project saved = projectRepository.save(project);

        // creator automatically becomes ADMIN member
        ProjectMember adminMember = new ProjectMember();
        adminMember.setProject(saved);
        adminMember.setUser(creator);
        adminMember.setRole(ProjectMember.ProjectRole.ADMIN);
        projectMemberRepository.save(adminMember);

        return saved;
    }

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public List<Project> getProjectsForUser(User user) {
        // projects they created
        List<Project> created = projectRepository.findByUserOrderByCreatedAtDesc(user);

        // projects they are a member of (but didn't create)
        List<Project> memberOf = projectMemberRepository
                .findByUserOrderByJoinedAtDesc(user)
                .stream()
                .map(ProjectMember::getProject)
                .filter(p -> !p.getUser().getId().equals(user.getId()))
                .toList();

        // merge both
        List<Project> all = new java.util.ArrayList<>(created);
        all.addAll(memberOf);
        return all;
    }

    public List<ProjectMember> getMembersOfProject(Long projectId) {
        Project project = findById(projectId);
        return projectMemberRepository.findByProject(project);
    }

    public void addMemberToProject(Long projectId, User userToAdd) {
        Project project = findById(projectId);
        if (projectMemberRepository.existsByProjectAndUser(project, userToAdd))
            throw new RuntimeException("User is already a member");

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(userToAdd);
        member.setRole(ProjectMember.ProjectRole.MEMBER);
        projectMemberRepository.save(member);
    }

    public void removeMemberFromProject(Long projectId, Long userId) {
        Project project = findById(projectId);
        projectMemberRepository.findByProject(project).stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .findFirst()
                .ifPresent(projectMemberRepository::delete);
    }

    public boolean isAdminOfProject(User user, Project project) {
        return projectMemberRepository.findByProjectAndUser(project, user)
                .map(m -> m.getRole() == ProjectMember.ProjectRole.ADMIN)
                .orElse(false);
    }

    public void updateProject(Long id, String name, String description, Project.ProjectStatus status) {
        Project project = findById(id);
        project.setName(name);
        project.setDescription(description);
        project.setStatus(status);
        projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}