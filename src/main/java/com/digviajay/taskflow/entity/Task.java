package com.digviajay.taskflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"project", "assignedUser", "subtasks"})
@EqualsAndHashCode(exclude = {"project", "assignedUser", "subtasks"})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;

    private LocalDate deadline;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(unique = true)
    private String taskKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedUser;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subtask> subtasks;

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    public enum TaskStatus {
        TODO, IN_PROGRESS, IN_REVIEW, DONE
    }

    @Enumerated(EnumType.STRING)
    private DevelopmentStatus developmentStatus;

    public enum DevelopmentStatus{
        NOT_STARTED,
        CODING,
        PR_OPEN,
        REVIEW_APPROVED,
        MERGED
    }
}