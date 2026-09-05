package com.digviajay.taskflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"createdProjects", "assignedTasks", "projectMemberships"})
@EqualsAndHashCode(exclude = {"createdProjects", "assignedTasks", "projectMemberships"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id",nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_role", nullable = false)
    private CompanyRole companyRole = CompanyRole.MEMBER;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Project> createdProjects;

    @OneToMany(mappedBy = "assignedUser")
    private List<Task> assignedTasks;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> projectMemberships;

    public enum CompanyRole {
        ADMIN, MEMBER
    }

}