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
import com.github.sharifrahim.onboard.dto.OperationalInfoRequest;
import com.github.sharifrahim.onboard.service.ApprovalService;
import com.github.sharifrahim.onboard.statemachine.OnboardingEvent;
import com.github.sharifrahim.onboard.statemachine.strategy.OnboardingStateMachineStrategy;
import com.github.sharifrahim.onboard.strategy.ValidationResult;

import lombok.RequiredArgsConstructor;

/**
 * Strategy for UPDATE_OPERATIONAL_INFO event
 */
@Component
@RequiredArgsConstructor
public class UpdateOperationalInfoStateMachineStrategy implements OnboardingStateMachineStrategy {

    private final ApprovalService approvalService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean validate(StateContext<ProgressState, OnboardingEvent> context) {
        OperationalInfoRequest request = context.getExtendedState().get("request", OperationalInfoRequest.class);
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
        OperationalInfoRequest request = context.getExtendedState().get("request", OperationalInfoRequest.class);
        Company company = context.getExtendedState().get("company", Company.class);

        // Create a copy of the existing company with updated operational info
        Company updated = Company.builder().id(company.getId()).name(company.getName())
                .registrationNumber(company.getRegistrationNumber()).entityType(company.getEntityType())
                .industrySector(company.getIndustrySector()).dateOfIncorporation(company.getDateOfIncorporation())
                .registeredAddress(company.getRegisteredAddress()).operatingAddress(company.getOperatingAddress())
                .country(company.getCountry()).progressState(ProgressState.OPERATIONS) // Advance to next state
                .companySize(company.getCompanySize()).description(company.getDescription())
                .mainContactName(company.getMainContactName()).mainContactEmail(company.getMainContactEmail())
                .mainContactPhone(company.getMainContactPhone()).contactPersonRole(company.getContactPersonRole())
                .secondaryContactName(company.getSecondaryContactName())
                .technicalContactEmail(company.getTechnicalContactEmail())
                .billingContactEmail(company.getBillingContactEmail()).authorizedPersons(company.getAuthorizedPersons())
                .emergencyContactNumber(company.getEmergencyContactNumber())
                .preferredLanguage(company.getPreferredLanguage()).build();

        // Set operational information
        updated.setTaxIdNumber(request.getTaxIdNumber());
        updated.setBankName(request.getBankName());
        updated.setBankAccountNumber(request.getBankAccountNumber());
        updated.setPreferredPaymentMethod(request.getPreferredPaymentMethod());
        updated.setRoleOnPlatform(request.getRoleOnPlatform());
        updated.setRequestedFeatures(request.getRequestedFeatures());
        updated.setOperatingHours(request.getOperatingHours());
        updated.setHasComplianceCertification(request.getHasComplianceCertification());
        updated.setAgreedToTermsOfService(request.getAgreedToTermsOfService());
        updated.setAgreedOnboardingDate(request.getAgreedOnboardingDate());

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
        return OnboardingEvent.UPDATE_OPERATIONAL_INFO;
    }

    private ValidationResult performValidation(OperationalInfoRequest request, Company company) {
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
        if (company.getProgressState() != ProgressState.CONTACT
                && company.getProgressState() != ProgressState.OPERATIONS) {
            result.addError("Cannot update operational info in current state: " + company.getProgressState());
        }

        // Business validations
        if (request.getTaxIdNumber() == null || request.getTaxIdNumber().trim().isEmpty()) {
            result.addError("Tax ID number is required");
        }

        if (request.getBankName() == null || request.getBankName().trim().isEmpty()) {
            result.addError("Bank name is required");
        }

        if (request.getBankAccountNumber() == null || request.getBankAccountNumber().trim().isEmpty()) {
            result.addError("Bank account number is required");
        }

        if (request.getPreferredPaymentMethod() == null || request.getPreferredPaymentMethod().trim().isEmpty()) {
            result.addError("Preferred payment method is required");
        }

        if (request.getRoleOnPlatform() == null || request.getRoleOnPlatform().trim().isEmpty()) {
            result.addError("Role on platform is required");
        }

        if (request.getOperatingHours() == null || request.getOperatingHours().trim().isEmpty()) {
            result.addError("Operating hours are required");
        }

        // Boolean validations
        if (request.getHasComplianceCertification() == null) {
            result.addError("Compliance certification status is required");
        }

        if (request.getAgreedToTermsOfService() == null) {
            result.addError("Terms of service agreement is required");
        }

        if (Boolean.TRUE.equals(request.getAgreedToTermsOfService()) && request.getAgreedOnboardingDate() == null) {
            result.addError("Agreed onboarding date is required when terms of service are accepted");
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
