package com.github.sharifrahim.onboard.strategy;

import org.springframework.stereotype.Component;

import com.github.sharifrahim.onboard.statemachine.OnboardingEvent;
import com.github.sharifrahim.onboard.strategy.impl.CreateCompanyStrategy;
import com.github.sharifrahim.onboard.strategy.impl.UpdateContactInfoStrategy;
import com.github.sharifrahim.onboard.strategy.impl.UpdateOperationalInfoStrategy;

import lombok.RequiredArgsConstructor;

/**
 * Factory for creating onboarding strategies
 */
@Component
@RequiredArgsConstructor
public class OnboardingStrategyFactory {

    private final CreateCompanyStrategy createCompanyStrategy;
    private final UpdateContactInfoStrategy updateContactInfoStrategy;
    private final UpdateOperationalInfoStrategy updateOperationalInfoStrategy;

    @SuppressWarnings("unchecked")
    public <T> OnboardingStrategy<T> getStrategy(OnboardingEvent event) {
        return switch (event) {
        case CREATE_COMPANY -> (OnboardingStrategy<T>) createCompanyStrategy;
        case UPDATE_CONTACT_INFO -> (OnboardingStrategy<T>) updateContactInfoStrategy;
        case UPDATE_OPERATIONAL_INFO -> (OnboardingStrategy<T>) updateOperationalInfoStrategy;
        default -> throw new IllegalArgumentException("No strategy found for event: " + event);
        };
    }
}
