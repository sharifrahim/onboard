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
import com.github.sharifrahim.onboard.dto.ContactInfoRequest;
import com.github.sharifrahim.onboard.service.ApprovalService;
import com.github.sharifrahim.onboard.statemachine.OnboardingEvent;
import com.github.sharifrahim.onboard.statemachine.strategy.OnboardingStateMachineStrategy;
import com.github.sharifrahim.onboard.strategy.ValidationResult;

import lombok.RequiredArgsConstructor;

/**
 * Strategy for UPDATE_CONTACT_INFO event
 */
@Component
@RequiredArgsConstructor
public class UpdateContactInfoStateMachineStrategy implements OnboardingStateMachineStrategy {

    private final ApprovalService approvalService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean validate(StateContext<ProgressState, OnboardingEvent> context) {
        ContactInfoRequest request = context.getExtendedState().get("request", ContactInfoRequest.class);
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
        ContactInfoRequest request = context.getExtendedState().get("request", ContactInfoRequest.class);
        Company company = context.getExtendedState().get("company", Company.class);

        // Create a copy of the existing company with updated contact info
        Company updated = Company.builder().id(company.getId()).name(company.getName())
                .registrationNumber(company.getRegistrationNumber()).entityType(company.getEntityType())
                .industrySector(company.getIndustrySector()).dateOfIncorporation(company.getDateOfIncorporation())
                .registeredAddress(company.getRegisteredAddress()).operatingAddress(company.getOperatingAddress())
                .country(company.getCountry()).progressState(ProgressState.CONTACT) // Advance to next state
                .companySize(company.getCompanySize()).description(company.getDescription()).build();

        // Set contact information
        updated.setMainContactName(request.getMainContactName());
        updated.setMainContactEmail(request.getMainContactEmail());
        updated.setMainContactPhone(request.getMainContactPhone());
        updated.setContactPersonRole(request.getContactPersonRole());
        updated.setSecondaryContactName(request.getSecondaryContactName());
        updated.setTechnicalContactEmail(request.getTechnicalContactEmail());
        updated.setBillingContactEmail(request.getBillingContactEmail());
        updated.setAuthorizedPersons(request.getAuthorizedPersons());
        updated.setEmergencyContactNumber(request.getEmergencyContactNumber());
        updated.setPreferredLanguage(request.getPreferredLanguage());

        // Create approval record
        Approval approval = Approval.builder().dataType("COMPANY").type(Approval.Type.CREATE_COMPANY)
                .dataId(company.getId()).operationType(OperationType.UPDATE).submittedBy("system")
                .submittedAt(LocalDateTime.now()).approvalStatus(ApprovalStatus.PENDING).newData(toJson(updated))
                .oldData(toJson(company)).build();

        Approval saved = approvalService.save(approval);

        // Store the result in extended state
        context.getExtendedState().getVariables().put("approvalId", saved.getId());
        context.getExtendedState().getVariables().put("updatedCompany", updated);
    }

    @Override
    public OnboardingEvent getEvent() {
        return OnboardingEvent.UPDATE_CONTACT_INFO;
    }

    private ValidationResult performValidation(ContactInfoRequest request, Company company) {
        ValidationResult result = new ValidationResult();

        // Validate that company exists
        if (company == null) {
            result.addError("Company does not exist");
            return result;
        }

        if (request == null) {
            result.addError("Request cannot be null");
            return result;
        }

        // Validate that company is in the right state
        if (company.getProgressState() != ProgressState.PROFILE
                && company.getProgressState() != ProgressState.CONTACT) {
            result.addError("Cannot update contact info in current state: " + company.getProgressState());
        }

        // Business validations
        if (request.getMainContactName() == null || request.getMainContactName().trim().isEmpty()) {
            result.addError("Main contact name is required");
        }

        if (request.getMainContactEmail() == null || request.getMainContactEmail().trim().isEmpty()) {
            result.addError("Main contact email is required");
        }

        if (request.getMainContactPhone() == null || request.getMainContactPhone().trim().isEmpty()) {
            result.addError("Main contact phone is required");
        }

        if (request.getContactPersonRole() == null || request.getContactPersonRole().trim().isEmpty()) {
            result.addError("Contact person role is required");
        }

        // Email validation (basic)
        if (request.getMainContactEmail() != null && !request.getMainContactEmail().contains("@")) {
            result.addError("Invalid main contact email format");
        }

        if (request.getTechnicalContactEmail() != null && !request.getTechnicalContactEmail().trim().isEmpty()
                && !request.getTechnicalContactEmail().contains("@")) {
            result.addError("Invalid technical contact email format");
        }

        if (request.getBillingContactEmail() != null && !request.getBillingContactEmail().trim().isEmpty()
                && !request.getBillingContactEmail().contains("@")) {
            result.addError("Invalid billing contact email format");
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
