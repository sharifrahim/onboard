package com.github.sharifrahim.onboard.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sharifrahim.onboard.domain.Approval;
import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.dto.CompanyProfileRequest;
import com.github.sharifrahim.onboard.dto.ContactInfoRequest;
import com.github.sharifrahim.onboard.dto.OperationalInfoRequest;
import com.github.sharifrahim.onboard.repository.CompanyRepository;
import com.github.sharifrahim.onboard.service.ApprovalService;
import com.github.sharifrahim.onboard.service.approval.ApprovalProcessor;
import com.github.sharifrahim.onboard.service.approval.ApprovalProcessorRegistry;
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
    private final ApprovalProcessorRegistry approvalProcessorRegistry;

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

    @GetMapping("/approvals")
    public ResponseEntity<List<Approval>> getApprovalsByType(@RequestParam Approval.Type type) {
        List<Approval> approvals = approvalService.findByType(type);
        return ResponseEntity.ok(approvals);
    }

    @PostMapping("/approvals/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        Optional<Approval> optional = approvalService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Approval approval = optional.get();

        try {
            Optional<ApprovalProcessor> processorOpt = approvalProcessorRegistry.findProcessor(approval.getType());
            if (processorOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            ApprovalProcessor processor = processorOpt.get();
            processor.approve(approval);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/approvals/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id, @RequestParam(required = false) String reason) {
        Optional<Approval> optional = approvalService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Approval approval = optional.get();

        try {
            Optional<ApprovalProcessor> processorOpt = approvalProcessorRegistry.findProcessor(approval.getType());
            if (processorOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            ApprovalProcessor processor = processorOpt.get();
            processor.reject(approval, reason != null ? reason : "No reason provided");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
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
