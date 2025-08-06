package com.github.sharifrahim.onboard.strategy.impl;

import org.springframework.stereotype.Component;

import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.domain.ProgressState;
import com.github.sharifrahim.onboard.dto.ContactInfoRequest;
import com.github.sharifrahim.onboard.strategy.OnboardingStrategy;
import com.github.sharifrahim.onboard.strategy.ValidationResult;

/**
 * Strategy for updating company contact information
 */
@Component
public class UpdateContactInfoStrategy implements OnboardingStrategy<ContactInfoRequest> {

    @Override
    public ValidationResult validate(ContactInfoRequest request, Company company) {
        ValidationResult result = new ValidationResult();

        // Validate that company exists
        if (company == null) {
            result.addError("Company does not exist");
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

    @Override
    public Company onSuccess(ContactInfoRequest request, Company company) {
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

        return updated;
    }
}
