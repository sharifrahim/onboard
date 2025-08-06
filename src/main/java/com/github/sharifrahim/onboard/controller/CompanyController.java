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
import com.github.sharifrahim.onboard.dto.CompanyProfileRequest;
import com.github.sharifrahim.onboard.dto.ContactInfoRequest;
import com.github.sharifrahim.onboard.dto.OperationalInfoRequest;
import com.github.sharifrahim.onboard.repository.CompanyRepository;
import com.github.sharifrahim.onboard.service.ApprovalService;
import com.github.sharifrahim.onboard.statemachine.OnboardingEvent;
import com.github.sharifrahim.onboard.statemachine.service.OnboardingStateMachineService;
import com.github.sharifrahim.onboard.exception.ValidationException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final ApprovalService approvalService;
    private final ObjectMapper objectMapper;
    private final OnboardingStateMachineService stateMachineService;

    @PostMapping("/profile")
    public ResponseEntity<Long> createCompany(@Valid @RequestBody CompanyProfileRequest request) {
        try {
            Long approvalId = stateMachineService.submitEvent(OnboardingEvent.CREATE_COMPANY, request, null);
            return new ResponseEntity<>(approvalId, HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/contact")
    public ResponseEntity<Long> updateContactInfo(@PathVariable Long id,
            @Valid @RequestBody ContactInfoRequest request) {
        Optional<Company> optional = companyRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Long approvalId = stateMachineService.submitEvent(OnboardingEvent.UPDATE_CONTACT_INFO, request,
                    optional.get());
            return ResponseEntity.ok(approvalId);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/operations")
    public ResponseEntity<Long> updateOperationalInfo(@PathVariable Long id,
            @Valid @RequestBody OperationalInfoRequest request) {
        Optional<Company> optional = companyRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Long approvalId = stateMachineService.submitEvent(OnboardingEvent.UPDATE_OPERATIONAL_INFO, request,
                    optional.get());
            return ResponseEntity.ok(approvalId);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
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

    @PostMapping("/approvals/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        Optional<Approval> optional = approvalService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Approval approval = optional.get();
        Company company = fromJson(approval.getNewData());

        if (approval.getOperationType() == OperationType.NEW) {
            Company savedCompany = companyRepository.save(company);
            approval.setDataId(savedCompany.getId());
        } else if (approval.getOperationType() == OperationType.UPDATE) {
            company.setId(approval.getDataId());
            companyRepository.save(company);
        }

        approval.setApprovalStatus(ApprovalStatus.APPROVED);
        approval.setApprovedBy("system");
        approval.setApprovedAt(LocalDateTime.now());
        approvalService.save(approval);

        return ResponseEntity.ok().build();
    }

    private Company fromJson(String json) {
        try {
            return objectMapper.readValue(json, Company.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse json", e);
        }
    }
}
