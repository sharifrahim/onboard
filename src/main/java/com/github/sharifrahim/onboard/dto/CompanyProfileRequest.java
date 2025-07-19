package com.github.sharifrahim.onboard.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CompanyProfileRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String registrationNumber;

    @NotBlank
    private String entityType;

    @NotBlank
    private String industrySector;

    @NotNull
    private LocalDate dateOfIncorporation;

    @NotBlank
    private String registeredAddress;

    private String operatingAddress;

    @NotBlank
    private String country;

    @NotBlank
    private String companySize;

    @NotBlank
    private String description;
}
