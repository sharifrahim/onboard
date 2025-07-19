package com.github.sharifrahim.onboard.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String registrationNumber;

    private String entityType;

    private String industrySector;

    private LocalDate dateOfIncorporation;

    private String registeredAddress;

    private String operatingAddress;

    private String country;

    private String companySize;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private String mainContactName;

    private String mainContactEmail;

    private String mainContactPhone;

    private String contactPersonRole;

    private String secondaryContactName;

    private String technicalContactEmail;

    private String billingContactEmail;

    private String authorizedPersons;

    private String emergencyContactNumber;

    private String preferredLanguage;

    private String taxIdNumber;

    private String bankName;

    private String bankAccountNumber;

    private String preferredPaymentMethod;

    private String roleOnPlatform;

    private String requestedFeatures;

    private String operatingHours;

    private Boolean hasComplianceCertification;

    private Boolean agreedToTermsOfService;

    private LocalDate agreedOnboardingDate;
}
