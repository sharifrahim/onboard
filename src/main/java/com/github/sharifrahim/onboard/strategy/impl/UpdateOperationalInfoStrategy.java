package com.github.sharifrahim.onboard.strategy.impl;

import org.springframework.stereotype.Component;

import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.domain.ProgressState;
import com.github.sharifrahim.onboard.dto.OperationalInfoRequest;
import com.github.sharifrahim.onboard.strategy.OnboardingStrategy;
import com.github.sharifrahim.onboard.strategy.ValidationResult;

/**
 * Strategy for updating company operational information
 */
@Component
public class UpdateOperationalInfoStrategy implements OnboardingStrategy<OperationalInfoRequest> {

    @Override
    public ValidationResult validate(OperationalInfoRequest request, Company company) {
        ValidationResult result = new ValidationResult();

        // Validate that company exists
        if (company == null) {
            result.addError("Company does not exist");
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

    @Override
    public Company onSuccess(OperationalInfoRequest request, Company company) {
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

        return updated;
    }
}
