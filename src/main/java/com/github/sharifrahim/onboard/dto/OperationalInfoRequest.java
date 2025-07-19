package com.github.sharifrahim.onboard.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OperationalInfoRequest {
    @NotBlank
    private String taxIdNumber;

    @NotBlank
    private String bankName;

    @NotBlank
    private String bankAccountNumber;

    @NotBlank
    private String preferredPaymentMethod;

    @NotBlank
    private String roleOnPlatform;

    @NotBlank
    private String requestedFeatures;

    @NotBlank
    private String operatingHours;

    @NotNull
    private Boolean hasComplianceCertification;

    @NotNull
    private Boolean agreedToTermsOfService;

    @NotNull
    private LocalDate agreedOnboardingDate;
}
