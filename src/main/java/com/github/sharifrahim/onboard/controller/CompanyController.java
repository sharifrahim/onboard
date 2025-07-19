package com.github.sharifrahim.onboard.controller;

import jakarta.validation.Valid;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.dto.CompanyProfileRequest;
import com.github.sharifrahim.onboard.dto.ContactInfoRequest;
import com.github.sharifrahim.onboard.dto.OperationalInfoRequest;
import com.github.sharifrahim.onboard.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;

    @PostMapping("/profile")
    public ResponseEntity<Long> createCompany(@Valid @RequestBody CompanyProfileRequest request) {
        Company company = Company.builder().name(request.getName()).registrationNumber(request.getRegistrationNumber())
                .entityType(request.getEntityType()).industrySector(request.getIndustrySector())
                .dateOfIncorporation(request.getDateOfIncorporation()).registeredAddress(request.getRegisteredAddress())
                .operatingAddress(request.getOperatingAddress()).country(request.getCountry())
                .companySize(request.getCompanySize()).description(request.getDescription()).build();
        Company saved = companyRepository.save(company);
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
        company.setMainContactName(request.getMainContactName());
        company.setMainContactEmail(request.getMainContactEmail());
        company.setMainContactPhone(request.getMainContactPhone());
        company.setContactPersonRole(request.getContactPersonRole());
        company.setSecondaryContactName(request.getSecondaryContactName());
        company.setTechnicalContactEmail(request.getTechnicalContactEmail());
        company.setBillingContactEmail(request.getBillingContactEmail());
        company.setAuthorizedPersons(request.getAuthorizedPersons());
        company.setEmergencyContactNumber(request.getEmergencyContactNumber());
        company.setPreferredLanguage(request.getPreferredLanguage());
        companyRepository.save(company);
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
        company.setTaxIdNumber(request.getTaxIdNumber());
        company.setBankName(request.getBankName());
        company.setBankAccountNumber(request.getBankAccountNumber());
        company.setPreferredPaymentMethod(request.getPreferredPaymentMethod());
        company.setRoleOnPlatform(request.getRoleOnPlatform());
        company.setRequestedFeatures(request.getRequestedFeatures());
        company.setOperatingHours(request.getOperatingHours());
        company.setHasComplianceCertification(request.getHasComplianceCertification());
        company.setAgreedToTermsOfService(request.getAgreedToTermsOfService());
        company.setAgreedOnboardingDate(request.getAgreedOnboardingDate());
        companyRepository.save(company);
        return ResponseEntity.ok().build();
    }
}
