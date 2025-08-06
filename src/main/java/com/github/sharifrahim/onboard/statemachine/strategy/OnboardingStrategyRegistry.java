package com.github.sharifrahim.onboard.statemachine.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import com.github.sharifrahim.onboard.domain.ProgressState;
import com.github.sharifrahim.onboard.statemachine.OnboardingEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Registry for managing onboarding strategies with flexible selection logic
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OnboardingStrategyRegistry {

    private final List<OnboardingStateMachineStrategy> strategies;

    /**
     * Find the appropriate strategy based on state, event, and context This allows for complex selection logic beyond
     * simple event mapping
     *
     * @param context
     *            the state machine context
     *
     * @return the strategy to use, or empty if none found
     */
    public Optional<OnboardingStateMachineStrategy> findStrategy(StateContext<ProgressState, OnboardingEvent> context) {

        ProgressState currentState = context.getSource().getId();
        OnboardingEvent event = context.getEvent();

        log.debug("Finding strategy for state: {} and event: {}", currentState, event);

        Optional<OnboardingStateMachineStrategy> strategy = strategies.stream()
                .filter(s -> s.canHandle(currentState, event, context))
                .sorted(Comparator.comparingInt(OnboardingStateMachineStrategy::getPriority)).findFirst();

        if (strategy.isPresent()) {
            log.debug("Selected strategy: {} with priority: {}", strategy.get().getClass().getSimpleName(),
                    strategy.get().getPriority());
        } else {
            log.warn("No strategy found for state: {} and event: {}", currentState, event);
        }

        return strategy;
    }

    /**
     * Find strategy by event (backward compatibility)
     *
     * @param event
     *            the onboarding event
     *
     * @return the strategy to use, or empty if none found
     */
    public Optional<OnboardingStateMachineStrategy> findStrategyByEvent(OnboardingEvent event) {
        log.debug("Finding strategy by event: {}", event);

        return strategies.stream().filter(strategy -> strategy.getEvent().equals(event))
                .sorted(Comparator.comparingInt(OnboardingStateMachineStrategy::getPriority)).findFirst();
    }

    /**
     * Get all available strategies
     *
     * @return list of all registered strategies
     */
    public List<OnboardingStateMachineStrategy> getAllStrategies() {
        return List.copyOf(strategies);
    }

    /**
     * Get all strategies that can handle a specific event
     *
     * @param event
     *            the onboarding event
     *
     * @return list of strategies that can handle the event
     */
    public List<OnboardingStateMachineStrategy> getStrategiesForEvent(OnboardingEvent event) {
        return strategies.stream().filter(strategy -> strategy.getEvent().equals(event))
                .sorted(Comparator.comparingInt(OnboardingStateMachineStrategy::getPriority)).toList();
    }
}
