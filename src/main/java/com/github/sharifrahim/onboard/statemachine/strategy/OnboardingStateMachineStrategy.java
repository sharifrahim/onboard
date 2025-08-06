package com.github.sharifrahim.onboard.statemachine.strategy;

import org.springframework.statemachine.StateContext;

import com.github.sharifrahim.onboard.domain.ProgressState;
import com.github.sharifrahim.onboard.statemachine.OnboardingEvent;

/**
 * Strategy interface for onboarding state machine operations with registry support
 */
public interface OnboardingStateMachineStrategy {

    /**
     * Validates the request before executing the action (Guard)
     *
     * @param context
     *            the state machine context
     *
     * @return true if validation passes, false otherwise
     */
    boolean validate(StateContext<ProgressState, OnboardingEvent> context);

    /**
     * Executes the business logic on successful validation (Action)
     *
     * @param context
     *            the state machine context
     */
    void onSuccess(StateContext<ProgressState, OnboardingEvent> context);

    /**
     * Returns the primary event this strategy handles
     *
     * @return the onboarding event
     */
    OnboardingEvent getEvent();

    /**
     * Determines if this strategy can handle the given state/event combination This enables more complex selection
     * logic than simple event mapping
     *
     * @param currentState
     *            the current state
     * @param event
     *            the event being processed
     * @param context
     *            the full state machine context for additional checks
     *
     * @return true if this strategy can handle the situation
     */
    default boolean canHandle(ProgressState currentState, OnboardingEvent event,
            StateContext<ProgressState, OnboardingEvent> context) {
        // Default implementation: handle if event matches
        return getEvent().equals(event);
    }

    /**
     * Returns the priority of this strategy when multiple strategies can handle the same scenario Lower numbers =
     * higher priority
     *
     * @return the priority (default: 100)
     */
    default int getPriority() {
        return 100;
    }
}
