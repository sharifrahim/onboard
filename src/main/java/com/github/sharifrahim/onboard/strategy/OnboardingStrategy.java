package com.github.sharifrahim.onboard.strategy;

import com.github.sharifrahim.onboard.domain.Company;

/**
 * Strategy interface for onboarding operations
 *
 * @param <T>
 *            the request type
 */
public interface OnboardingStrategy<T> {

    /**
     * Validates the request and current company state
     *
     * @param request
     *            the incoming request
     * @param company
     *            the current company (can be null for new company creation)
     *
     * @return validation result
     */
    ValidationResult validate(T request, Company company);

    /**
     * Executes the operation on successful validation
     *
     * @param request
     *            the incoming request
     * @param company
     *            the current company (can be null for new company creation)
     *
     * @return the updated company
     */
    Company onSuccess(T request, Company company);
}
