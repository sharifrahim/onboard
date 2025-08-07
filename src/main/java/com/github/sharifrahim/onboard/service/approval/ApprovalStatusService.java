package com.github.sharifrahim.onboard.service.approval;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.github.sharifrahim.onboard.domain.Approval;
import com.github.sharifrahim.onboard.service.ApprovalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Common service for handling approval status updates
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalStatusService {

    private final ApprovalService approvalService;

    /**
     * Marks an approval as approved and saves it
     */
    public Approval markAsApproved(Approval approval) {
        log.info("Marking approval {} as APPROVED", approval.getId());

        approval.setApprovalStatus(Approval.ApprovalStatus.APPROVED);
        approval.setApprovedBy("system");
        approval.setApprovedAt(LocalDateTime.now());

        return approvalService.save(approval);
    }

    /**
     * Marks an approval as rejected with a reason and saves it
     */
    public Approval markAsRejected(Approval approval, String reason) {
        log.info("Marking approval {} as REJECTED with reason: {}", approval.getId(), reason);

        approval.setApprovalStatus(Approval.ApprovalStatus.REJECTED);
        approval.setApprovedBy("system");
        approval.setApprovedAt(LocalDateTime.now());
        approval.setRemarks(reason);

        return approvalService.save(approval);
    }

    /**
     * Updates the data ID for an approval (used when creating new entities)
     */
    public Approval updateDataId(Approval approval, Long dataId) {
        log.info("Updating approval {} with data ID: {}", approval.getId(), dataId);

        approval.setDataId(dataId);
        return approvalService.save(approval);
    }
}
