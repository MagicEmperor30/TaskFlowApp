package com.digviajay.taskflow.entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_invites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(unique = true, nullable = false)
    private String token; // UUID generated on creation

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt; // e.g. now + 7 days

    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}