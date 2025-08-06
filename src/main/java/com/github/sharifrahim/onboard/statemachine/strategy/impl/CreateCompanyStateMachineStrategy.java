package com.github.sharifrahim.onboard.statemachine.strategy.impl;

import java.time.LocalDateTime;

import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sharifrahim.onboard.domain.Approval;
import com.github.sharifrahim.onboard.domain.Approval.ApprovalStatus;
import com.github.sharifrahim.onboard.domain.Approval.OperationType;
import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.domain.ProgressState;
import com.github.sharifrahim.onboard.dto.CompanyProfileRequest;
import com.github.sharifrahim.onboard.service.ApprovalService;
import com.github.sharifrahim.onboard.statemachine.OnboardingEvent;
import com.github.sharifrahim.onboard.statemachine.strategy.OnboardingStateMachineStrategy;
import com.github.sharifrahim.onboard.strategy.ValidationResult;

import lombok.RequiredArgsConstructor;

/**
 * Strategy for CREATE_COMPANY event
 */
@Component
@RequiredArgsConstructor
public class CreateCompanyStateMachineStrategy implements OnboardingStateMachineStrategy {

    private final ApprovalService approvalService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean validate(StateContext<ProgressState, OnboardingEvent> context) {
        CompanyProfileRequest request = context.getExtendedState().get("request", CompanyProfileRequest.class);
        Company company = context.getExtendedState().get("company", Company.class);

        ValidationResult result = performValidation(request, company);

        if (!result.isValid()) {
            context.getExtendedState().getVariables().put("validationErrors", result.getErrors());
            return false;
        }

        return true;
    }

    @Override
    public void onSuccess(StateContext<ProgressState, OnboardingEvent> context) {
        CompanyProfileRequest request = context.getExtendedState().get("request", CompanyProfileRequest.class);

        // Create the company entity
        Company company = Company.builder().name(request.getName()).registrationNumber(request.getRegistrationNumber())
                .entityType(request.getEntityType()).industrySector(request.getIndustrySector())
                .dateOfIncorporation(request.getDateOfIncorporation()).registeredAddress(request.getRegisteredAddress())
                .operatingAddress(request.getOperatingAddress()).country(request.getCountry())
                .progressState(ProgressState.PROFILE).companySize(request.getCompanySize())
                .description(request.getDescription()).build();

        // Create approval record
        Approval approval = Approval.builder().dataType("COMPANY").operationType(OperationType.NEW)
                .submittedBy("system").submittedAt(LocalDateTime.now()).approvalStatus(ApprovalStatus.PENDING)
                .newData(toJson(company)).build();

        Approval saved = approvalService.save(approval);

        // Store the result in extended state
        context.getExtendedState().getVariables().put("approvalId", saved.getId());
        context.getExtendedState().getVariables().put("updatedCompany", company);
    }

    @Override
    public OnboardingEvent getEvent() {
        return OnboardingEvent.CREATE_COMPANY;
    }

    private ValidationResult performValidation(CompanyProfileRequest request, Company company) {
        ValidationResult result = new ValidationResult();

        // Validate that this is a new company creation (company should be null)
        if (company != null) {
            result.addError("Company already exists, cannot create new profile");
        }

        // Additional business validations
        if (request == null) {
            result.addError("Request cannot be null");
            return result;
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            result.addError("Company name is required");
        }

        if (request.getRegistrationNumber() == null || request.getRegistrationNumber().trim().isEmpty()) {
            result.addError("Registration number is required");
        }

        if (request.getEntityType() == null || request.getEntityType().trim().isEmpty()) {
            result.addError("Entity type is required");
        }

        if (request.getCountry() == null || request.getCountry().trim().isEmpty()) {
            result.addError("Country is required");
        }

        return result;
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }
}
