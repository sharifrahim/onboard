package com.github.sharifrahim.onboard.service.approval.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sharifrahim.onboard.domain.Approval;
import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.repository.CompanyRepository;
import com.github.sharifrahim.onboard.service.ApprovalService;
import com.github.sharifrahim.onboard.service.approval.ApprovalProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes UPDATE_OPERATIONAL_INFO approval requests
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateOperationalInfoApprovalProcessor implements ApprovalProcessor {

    private final CompanyRepository companyRepository;
    private final ApprovalService approvalService;
    private final ObjectMapper objectMapper;

    @Override
    public Approval approve(Approval approval) {
        log.info("Processing approval for UPDATE_OPERATIONAL_INFO with ID: {}", approval.getId());

        try {
            // Parse the company data from JSON
            Company company = fromJson(approval.getNewData());
            company.setId(approval.getDataId());

            // Update the company
            Company savedCompany = companyRepository.save(company);
            log.info("Updated company operational info with ID: {}", savedCompany.getId());

            // Update approval status
            approval.setApprovalStatus(Approval.ApprovalStatus.APPROVED);
            approval.setApprovedBy("system");
            approval.setApprovedAt(LocalDateTime.now());

            return approvalService.save(approval);

        } catch (Exception e) {
            log.error("Error processing UPDATE_OPERATIONAL_INFO approval: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process UPDATE_OPERATIONAL_INFO approval", e);
        }
    }

    @Override
    public Approval reject(Approval approval, String reason) {
        log.info("Rejecting UPDATE_OPERATIONAL_INFO approval with ID: {} for reason: {}", approval.getId(), reason);

        approval.setApprovalStatus(Approval.ApprovalStatus.REJECTED);
        approval.setApprovedBy("system");
        approval.setApprovedAt(LocalDateTime.now());
        approval.setRemarks(reason);

        return approvalService.save(approval);
    }

    @Override
    public Approval.Type getType() {
        return Approval.Type.UPDATE_OPERATIONAL_INFO;
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
