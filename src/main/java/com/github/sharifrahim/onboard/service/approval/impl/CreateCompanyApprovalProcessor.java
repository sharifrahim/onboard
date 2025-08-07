package com.github.sharifrahim.onboard.service.approval.impl;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sharifrahim.onboard.domain.Approval;
import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.repository.CompanyRepository;
import com.github.sharifrahim.onboard.service.approval.ApprovalProcessor;
import com.github.sharifrahim.onboard.service.approval.ApprovalStatusService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes CREATE_COMPANY approval requests
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateCompanyApprovalProcessor implements ApprovalProcessor {

    private final CompanyRepository companyRepository;
    private final ApprovalStatusService approvalStatusService;
    private final ObjectMapper objectMapper;

    @Override
    public Approval approve(Approval approval) {
        log.info("Processing approval for CREATE_COMPANY with ID: {}", approval.getId());

        try {
            // Parse the company data from JSON
            Company company = fromJson(approval.getNewData());

            // Save the company
            Company savedCompany;
            if (approval.getOperationType() == Approval.OperationType.NEW) {
                savedCompany = companyRepository.save(company);
                approvalStatusService.updateDataId(approval, savedCompany.getId());
                log.info("Created new company with ID: {}", savedCompany.getId());
            } else if (approval.getOperationType() == Approval.OperationType.UPDATE) {
                company.setId(approval.getDataId());
                savedCompany = companyRepository.save(company);
                log.info("Updated company with ID: {}", savedCompany.getId());
            } else {
                throw new IllegalArgumentException("Unsupported operation type: " + approval.getOperationType());
            }

            // Update approval status using common service
            return approvalStatusService.markAsApproved(approval);

        } catch (Exception e) {
            log.error("Error processing CREATE_COMPANY approval: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process CREATE_COMPANY approval", e);
        }
    }

    @Override
    public Approval reject(Approval approval, String reason) {
        log.info("Rejecting CREATE_COMPANY approval with ID: {} for reason: {}", approval.getId(), reason);

        return approvalStatusService.markAsRejected(approval, reason);
    }

    @Override
    public Approval.Type getType() {
        return Approval.Type.CREATE_COMPANY;
    }

    private Company fromJson(String json) {
        try {
            return objectMapper.readValue(json, Company.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse company JSON: {}", json, e);
            throw new RuntimeException("Failed to parse company data", e);
        }
    }
}
