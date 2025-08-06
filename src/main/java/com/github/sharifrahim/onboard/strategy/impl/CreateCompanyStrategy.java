package com.github.sharifrahim.onboard.strategy.impl;

import org.springframework.stereotype.Component;

import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.domain.ProgressState;
import com.github.sharifrahim.onboard.dto.CompanyProfileRequest;
import com.github.sharifrahim.onboard.strategy.OnboardingStrategy;
import com.github.sharifrahim.onboard.strategy.ValidationResult;

/**
 * Strategy for creating a new company profile
 */
@Component
public class CreateCompanyStrategy implements OnboardingStrategy<CompanyProfileRequest> {

    @Override
    public ValidationResult validate(CompanyProfileRequest request, Company company) {
        ValidationResult result = new ValidationResult();

        // Validate that this is a new company creation (company should be null)
        if (company != null) {
            result.addError("Company already exists, cannot create new profile");
        }

        // Additional business validations
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

    @Override
    public Company onSuccess(CompanyProfileRequest request, Company company) {
        return Company.builder().name(request.getName()).registrationNumber(request.getRegistrationNumber())
                .entityType(request.getEntityType()).industrySector(request.getIndustrySector())
                .dateOfIncorporation(request.getDateOfIncorporation()).registeredAddress(request.getRegisteredAddress())
                .operatingAddress(request.getOperatingAddress()).country(request.getCountry())
                .progressState(ProgressState.PROFILE).companySize(request.getCompanySize())
                .description(request.getDescription()).build();
    }
}
