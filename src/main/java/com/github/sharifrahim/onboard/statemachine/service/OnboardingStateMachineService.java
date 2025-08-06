package com.github.sharifrahim.onboard.statemachine.service;

import java.util.List;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.stereotype.Service;

import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.domain.ProgressState;
import com.github.sharifrahim.onboard.exception.ValidationException;
import com.github.sharifrahim.onboard.statemachine.OnboardingEvent;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Service that wraps Spring State Machine for onboarding process
 */
@Service
@RequiredArgsConstructor
public class OnboardingStateMachineService {

    private final StateMachine<ProgressState, OnboardingEvent> stateMachine;

    /**
     * Submits an event to the state machine
     *
     * @param event
     *            the onboarding event
     * @param request
     *            the request data
     * @param company
     *            the current company (can be null for new company creation)
     *
     * @return the approval ID
     */
    public <T> Long submitEvent(OnboardingEvent event, T request, Company company) {
        // Reset the state machine to initial state for new processing
        if (!stateMachine.getState().getId().equals(ProgressState.PROFILE)) {
            stateMachine.stopReactively().block();
            stateMachine.startReactively().block();
        }

        // Put request and company data in extended state
        stateMachine.getExtendedState().getVariables().put("request", request);
        stateMachine.getExtendedState().getVariables().put("company", company);

        // Clear any previous validation errors
        stateMachine.getExtendedState().getVariables().remove("validationErrors");

        // Send the event
        StateMachineEventResult<ProgressState, OnboardingEvent> eventResult = stateMachine
                .sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).blockFirst();

        if (eventResult == null || eventResult.getResultType() != StateMachineEventResult.ResultType.ACCEPTED) {
            // Check if there were validation errors
            @SuppressWarnings("unchecked")
            List<String> validationErrors = (List<String>) stateMachine.getExtendedState().getVariables()
                    .get("validationErrors");

            if (validationErrors != null && !validationErrors.isEmpty()) {
                throw new ValidationException("Validation failed: " + String.join("; ", validationErrors));
            } else {
                throw new ValidationException("Event not accepted by state machine");
            }
        }

        // Get the approval ID from the extended state
        Long approvalId = (Long) stateMachine.getExtendedState().getVariables().get("approvalId");
        if (approvalId == null) {
            throw new RuntimeException("Approval ID not found in state machine extended state");
        }

        return approvalId;
    }
}
