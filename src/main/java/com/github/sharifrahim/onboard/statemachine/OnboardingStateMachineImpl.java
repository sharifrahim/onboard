package com.github.sharifrahim.onboard.statemachine;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sharifrahim.onboard.domain.Approval;
import com.github.sharifrahim.onboard.domain.Approval.ApprovalStatus;
import com.github.sharifrahim.onboard.domain.Approval.OperationType;
import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.service.ApprovalService;
import com.github.sharifrahim.onboard.strategy.OnboardingStrategy;
import com.github.sharifrahim.onboard.exception.ValidationException;
import com.github.sharifrahim.onboard.strategy.ValidationResult;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the onboarding state machine
 */
@Service
@RequiredArgsConstructor
public class OnboardingStateMachineImpl implements OnboardingStateMachine {

    private final ApprovalService approvalService;
    private final ObjectMapper objectMapper;

    @Override
    public <T> Long submitEvent(OnboardingEvent event, OnboardingStrategy<T> strategy, T request, Company company) {
        // Guard: validate the request
        ValidationResult validationResult = strategy.validate(request, company);
        if (!validationResult.isValid()) {
            throw new ValidationException("Validation failed: " + validationResult.getErrorMessage());
        }

        // Action: execute the strategy
        Company updatedCompany = strategy.onSuccess(request, company);

        // Create approval record
        Approval approval = createApproval(event, updatedCompany, company);
        Approval saved = approvalService.save(approval);

        return saved.getId();
    }

    private Approval createApproval(OnboardingEvent event, Company newCompany, Company oldCompany) {
        OperationType operationType = getOperationType(event);

        Approval.ApprovalBuilder builder = Approval.builder().dataType("COMPANY").operationType(operationType)
                .submittedBy("system").submittedAt(LocalDateTime.now()).approvalStatus(ApprovalStatus.PENDING)
                .newData(toJson(newCompany));

        if (oldCompany != null) {
            builder.dataId(oldCompany.getId()).oldData(toJson(oldCompany));
        }

        return builder.build();
    }

    private OperationType getOperationType(OnboardingEvent event) {
        return switch (event) {
        case CREATE_COMPANY -> OperationType.NEW;
        case UPDATE_CONTACT_INFO, UPDATE_OPERATIONAL_INFO -> OperationType.UPDATE;
        default -> throw new IllegalArgumentException("Unsupported event: " + event);
        };
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }
}
