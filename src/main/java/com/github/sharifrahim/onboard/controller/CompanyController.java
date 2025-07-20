package com.github.sharifrahim.onboard.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sharifrahim.onboard.domain.Approval;
import com.github.sharifrahim.onboard.domain.Approval.ApprovalStatus;
import com.github.sharifrahim.onboard.domain.Approval.OperationType;
import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.domain.ProgressState;
import com.github.sharifrahim.onboard.dto.CompanyProfileRequest;
import com.github.sharifrahim.onboard.dto.ContactInfoRequest;
import com.github.sharifrahim.onboard.dto.OperationalInfoRequest;
import com.github.sharifrahim.onboard.repository.CompanyRepository;
import com.github.sharifrahim.onboard.service.ApprovalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final ApprovalService approvalService;
    private final ObjectMapper objectMapper;

    @PostMapping("/profile")
    public ResponseEntity<Long> createCompany(@Valid @RequestBody CompanyProfileRequest request) {
        Company company = Company.builder().name(request.getName()).registrationNumber(request.getRegistrationNumber())
                .entityType(request.getEntityType()).industrySector(request.getIndustrySector())
                .dateOfIncorporation(request.getDateOfIncorporation()).registeredAddress(request.getRegisteredAddress())
                .operatingAddress(request.getOperatingAddress()).country(request.getCountry())
                .progressState(ProgressState.PROFILE).companySize(request.getCompanySize())
                .description(request.getDescription()).build();
        Approval approval = Approval.builder().dataType("COMPANY").operationType(OperationType.NEW)
                .submittedBy("system").submittedAt(LocalDateTime.now()).approvalStatus(ApprovalStatus.PENDING)
                .newData(toJson(company)).build();
        Approval saved = approvalService.save(approval);
        return new ResponseEntity<>(saved.getId(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/contact")
    public ResponseEntity<Void> updateContactInfo(@PathVariable Long id,
            @Valid @RequestBody ContactInfoRequest request) {
        Optional<Company> optional = companyRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Company company = optional.get();
        Company updated = Company.builder().id(company.getId()).name(company.getName())
                .registrationNumber(company.getRegistrationNumber()).entityType(company.getEntityType())
                .industrySector(company.getIndustrySector()).dateOfIncorporation(company.getDateOfIncorporation())
                .registeredAddress(company.getRegisteredAddress()).operatingAddress(company.getOperatingAddress())
                .country(company.getCountry()).progressState(company.getProgressState())
                .companySize(company.getCompanySize()).description(company.getDescription()).build();
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

        Approval approval = Approval.builder().dataType("COMPANY").dataId(company.getId())
                .operationType(OperationType.UPDATE).submittedBy("system").submittedAt(LocalDateTime.now())
                .approvalStatus(ApprovalStatus.PENDING).newData(toJson(updated)).oldData(toJson(company)).build();
        approvalService.save(approval);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/operations")
    public ResponseEntity<Void> updateOperationalInfo(@PathVariable Long id,
            @Valid @RequestBody OperationalInfoRequest request) {
        Optional<Company> optional = companyRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Company company = optional.get();
        Company updated = Company.builder().id(company.getId()).name(company.getName())
                .registrationNumber(company.getRegistrationNumber()).entityType(company.getEntityType())
                .industrySector(company.getIndustrySector()).dateOfIncorporation(company.getDateOfIncorporation())
                .registeredAddress(company.getRegisteredAddress()).operatingAddress(company.getOperatingAddress())
                .country(company.getCountry()).progressState(company.getProgressState())
                .companySize(company.getCompanySize()).description(company.getDescription())
                .mainContactName(company.getMainContactName()).mainContactEmail(company.getMainContactEmail())
                .mainContactPhone(company.getMainContactPhone()).contactPersonRole(company.getContactPersonRole())
                .secondaryContactName(company.getSecondaryContactName())
                .technicalContactEmail(company.getTechnicalContactEmail())
                .billingContactEmail(company.getBillingContactEmail()).authorizedPersons(company.getAuthorizedPersons())
                .emergencyContactNumber(company.getEmergencyContactNumber())
                .preferredLanguage(company.getPreferredLanguage()).build();
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

        Approval approval = Approval.builder().dataType("COMPANY").dataId(company.getId())
                .operationType(OperationType.UPDATE).submittedBy("system").submittedAt(LocalDateTime.now())
                .approvalStatus(ApprovalStatus.PENDING).newData(toJson(updated)).oldData(toJson(company)).build();
        approvalService.save(approval);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/approvals/{id}/restore")
    public ResponseEntity<Company> restoreFromApproval(@PathVariable Long id) {
        Optional<Approval> optional = approvalService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Approval approval = optional.get();
        Company company = fromJson(approval.getNewData());
        return ResponseEntity.ok(company);
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    private Company fromJson(String json) {
        try {
            return objectMapper.readValue(json, Company.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse json", e);
        }
    }
}
