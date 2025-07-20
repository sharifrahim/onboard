package com.github.sharifrahim.onboard.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "approval_table")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dataType;

    private Long dataId;

    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    private String submittedBy;

    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    private String approvedBy;

    private LocalDateTime approvedAt;

    @Column(columnDefinition = "jsonb")
    private String newData;

    @Column(columnDefinition = "jsonb")
    private String oldData;

    private String changeSummary;

    private String remarks;

    public enum OperationType {
        NEW, UPDATE
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
}
