package com.github.sharifrahim.onboard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ContactInfoRequest {
    @NotBlank
    private String mainContactName;

    @Email
    @NotBlank
    private String mainContactEmail;

    @NotBlank
    private String mainContactPhone;

    @NotBlank
    private String contactPersonRole;

    private String secondaryContactName;

    @Email
    @NotBlank
    private String technicalContactEmail;

    @Email
    @NotBlank
    private String billingContactEmail;

    @NotBlank
    private String authorizedPersons;

    @NotBlank
    private String emergencyContactNumber;

    @NotBlank
    private String preferredLanguage;
}
