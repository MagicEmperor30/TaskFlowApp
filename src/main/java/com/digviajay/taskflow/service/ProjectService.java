package com.digviajay.taskflow.service;

import com.digviajay.taskflow.entity.Company;
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
        project.setCompany(creator.getCompany());         // V2: scope to company
        Project saved = projectRepository.save(project);

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

    // V2: all projects user is a member of (includes ones they created as ADMIN)
    public List<Project> getProjectsForUser(User user) {
        return projectMemberRepository
                .findByUserOrderByJoinedAtDesc(user)
                .stream()
                .map(ProjectMember::getProject)
                .toList();
    }

    // V2: all projects in a company (for company overview page)
    public List<Project> getProjectsForCompany(Company company) {
        return projectRepository.findByCompanyOrderByCreatedAtDesc(company);
    }

    public List<ProjectMember> getMembersOfProject(Long projectId) {
        Project project = findById(projectId);
        return projectMemberRepository.findByProject(project);
    }

    // V2: only search users from same company
    public void addMemberToProject(Long projectId, User userToAdd, User requestingUser) {
        Project project = findById(projectId);

        if (!project.getCompany().getId().equals(userToAdd.getCompany().getId()))
            throw new RuntimeException("Cannot add user from a different company");

        if (!isAdminOfProject(requestingUser, project))
            throw new RuntimeException("Only project admin can add members");

        if (projectMemberRepository.existsByProjectAndUser(project, userToAdd))
            throw new RuntimeException("User is already a member");

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(userToAdd);
        member.setRole(ProjectMember.ProjectRole.MEMBER);
        projectMemberRepository.save(member);
    }

    public void removeMemberFromProject(Long projectId, Long userId, User requestingUser) {
        Project project = findById(projectId);

        if (!isAdminOfProject(requestingUser, project))
            throw new RuntimeException("Only project admin can remove members");

        // prevent admin removing themselves
        if (requestingUser.getId().equals(userId))
            throw new RuntimeException("Project admin cannot remove themselves");

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

    // V2: check user belongs to same company as project before any access
    public boolean isMemberOfProject(User user, Project project) {
        return projectMemberRepository.existsByProjectAndUser(project, user);
    }

    public void updateProject(Long id, String name, String description,
                              Project.ProjectStatus status, User requestingUser) {
        Project project = findById(id);

        if (!isAdminOfProject(requestingUser, project))
            throw new RuntimeException("Only project admin can update project");

        project.setName(name);
        project.setDescription(description);
        project.setStatus(status);
        projectRepository.save(project);
    }

    public void deleteProject(Long id, User requestingUser) {
        Project project = findById(id);

        if (!isAdminOfProject(requestingUser, project))
            throw new RuntimeException("Only project admin can delete project");

        projectRepository.deleteById(id);
    }
}