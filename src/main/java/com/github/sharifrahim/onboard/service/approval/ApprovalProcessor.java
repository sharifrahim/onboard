package com.github.sharifrahim.onboard.service.approval;

import com.github.sharifrahim.onboard.domain.Approval;

/**
 * Interface for processing approval requests based on approval type
 */
public interface ApprovalProcessor {

    /**
     * Process an approval request
     *
     * @param approval
     *            the approval to process
     *
     * @return the processed approval
     */
    Approval approve(Approval approval);

    /**
     * Process a rejection request
     *
     * @param approval
     *            the approval to reject
     * @param reason
     *            the reason for rejection
     *
     * @return the processed approval
     */
    Approval reject(Approval approval, String reason);

    /**
     * Get the approval type this processor handles
     *
     * @return the approval type
     */
    Approval.Type getType();

    /**
     * Check if this processor can handle the given approval type
     *
     * @param type
     *            the approval type
     *
     * @return true if this processor can handle the type
     */
    default boolean canHandle(Approval.Type type) {
        return getType().equals(type);
    }
}
