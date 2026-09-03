package com.digviajay.taskflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Entity
@Table(name="development_activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevelopmentActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="task_id",nullable = false)
    private Task task;

    @Column(nullable = false)
    private String gitHubUsername;

    @Column(nullable = false)
    private String branchName;

    @Column(length = 1000)
    private String commitMessage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumType evenType;

    private LocalDateTime activityTime;

    public enum EvenType{
        PUSH,
        NOT_STARTED,
        CODING,
        PR_OPEN,
        REVIEW_APPROVED,
        MERGED
    }

}
