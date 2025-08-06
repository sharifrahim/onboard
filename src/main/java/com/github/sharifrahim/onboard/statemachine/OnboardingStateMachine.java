package com.github.sharifrahim.onboard.statemachine;

import com.github.sharifrahim.onboard.domain.Company;
import com.github.sharifrahim.onboard.strategy.OnboardingStrategy;

/**
 * State machine for company onboarding process
 */
public interface OnboardingStateMachine {

    /**
     * Submits an event to the state machine
     *
     * @param event
     *            the onboarding event
     * @param strategy
     *            the strategy to handle the event
     * @param request
     *            the request data
     * @param company
     *            the current company
     *
     * @return the approval ID
     */
    <T> Long submitEvent(OnboardingEvent event, OnboardingStrategy<T> strategy, T request, Company company);
}
