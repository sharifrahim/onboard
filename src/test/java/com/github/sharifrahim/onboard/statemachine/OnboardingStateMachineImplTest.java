package com.github.sharifrahim.onboard.statemachine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sharifrahim.onboard.domain.Approval;
import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.domain.ProgressState;
import com.github.sharifrahim.onboard.dto.CompanyProfileRequest;
import com.github.sharifrahim.onboard.exception.ValidationException;
import com.github.sharifrahim.onboard.service.ApprovalService;
import com.github.sharifrahim.onboard.strategy.OnboardingStrategy;
import com.github.sharifrahim.onboard.strategy.ValidationResult;

class OnboardingStateMachineImplTest {

    @Mock
    private ApprovalService approvalService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OnboardingStrategy<CompanyProfileRequest> strategy;

    private OnboardingStateMachineImpl stateMachine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stateMachine = new OnboardingStateMachineImpl(approvalService, objectMapper);
    }

    @Test
    void testSubmitEvent_ValidationSuccess() throws Exception {
        // Given
        CompanyProfileRequest request = new CompanyProfileRequest();
        request.setName("Test Company");

        Company expectedCompany = Company.builder().name("Test Company").progressState(ProgressState.PROFILE).build();

        Approval approval = new Approval();
        approval.setId(123L);

        when(strategy.validate(request, null)).thenReturn(ValidationResult.success());
        when(strategy.onSuccess(request, null)).thenReturn(expectedCompany);
        when(objectMapper.writeValueAsString(expectedCompany)).thenReturn("{\"name\":\"Test Company\"}");
        when(approvalService.save(any(Approval.class))).thenReturn(approval);

        // When
        Long result = stateMachine.submitEvent(OnboardingEvent.CREATE_COMPANY, strategy, request, null);

        // Then
        assertEquals(123L, result);
        verify(strategy).validate(request, null);
        verify(strategy).onSuccess(request, null);
        verify(approvalService).save(any(Approval.class));
    }

    @Test
    void testSubmitEvent_ValidationFailure() {
        // Given
        CompanyProfileRequest request = new CompanyProfileRequest();
        ValidationResult validationResult = ValidationResult.failure("Name is required");

        when(strategy.validate(request, null)).thenReturn(validationResult);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> stateMachine.submitEvent(OnboardingEvent.CREATE_COMPANY, strategy, request, null));

        assertEquals("Validation failed: Name is required", exception.getMessage());
        verify(strategy).validate(request, null);
        verify(strategy, never()).onSuccess(any(), any());
        verify(approvalService, never()).save(any());
    }
}
