package com.github.sharifrahim.onboard.statemachine.config;

import java.util.Optional;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.guard.Guard;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import com.github.sharifrahim.onboard.domain.ProgressState;
import com.github.sharifrahim.onboard.statemachine.OnboardingEvent;
import com.github.sharifrahim.onboard.statemachine.strategy.OnboardingStateMachineStrategy;
import com.github.sharifrahim.onboard.statemachine.strategy.OnboardingStrategyRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring State Machine configuration for onboarding process using registry pattern
 */
@Configuration
@EnableStateMachine
@RequiredArgsConstructor
@Slf4j
public class OnboardingStateMachineConfig extends StateMachineConfigurerAdapter<ProgressState, OnboardingEvent> {

    private final OnboardingStrategyRegistry strategyRegistry;

    /**
     * Generic guard method that delegates to the appropriate strategy using registry
     */
    private boolean validate(StateContext<ProgressState, OnboardingEvent> context) {
        log.debug("Validating for state: {} and event: {}", context.getSource().getId(), context.getEvent());

        Optional<OnboardingStateMachineStrategy> strategy = strategyRegistry.findStrategy(context);

        if (strategy.isPresent()) {
            return strategy.get().validate(context);
        } else {
            log.warn("No strategy found for validation - state: {}, event: {}", context.getSource().getId(),
                    context.getEvent());
            return false;
        }
    }

    /**
     * Generic action method that delegates to the appropriate strategy using registry
     */
    private void onSuccess(StateContext<ProgressState, OnboardingEvent> context) {
        log.debug("Executing onSuccess for state: {} and event: {}", context.getSource().getId(), context.getEvent());

        Optional<OnboardingStateMachineStrategy> strategy = strategyRegistry.findStrategy(context);

        if (strategy.isPresent()) {
            strategy.get().onSuccess(context);
        } else {
            log.error("No strategy found for onSuccess - state: {}, event: {}", context.getSource().getId(),
                    context.getEvent());
            throw new IllegalStateException("No strategy found for event: " + context.getEvent());
        }
    }

    // Guard instances
    private final Guard<ProgressState, OnboardingEvent> genericGuard = this::validate;

    // Action instances
    private final Action<ProgressState, OnboardingEvent> genericAction = this::onSuccess;

    @Override
    public void configure(StateMachineStateConfigurer<ProgressState, OnboardingEvent> states) throws Exception {
        states.withStates().initial(ProgressState.PROFILE).state(ProgressState.CONTACT).state(ProgressState.OPERATIONS)
                .end(ProgressState.COMPLETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ProgressState, OnboardingEvent> transitions)
            throws Exception {
        transitions.withExternal().source(ProgressState.PROFILE).target(ProgressState.PROFILE)
                .event(OnboardingEvent.CREATE_COMPANY).guard(genericGuard).action(genericAction).and().withExternal()
                .source(ProgressState.PROFILE).target(ProgressState.CONTACT).event(OnboardingEvent.UPDATE_CONTACT_INFO)
                .guard(genericGuard).action(genericAction).and().withExternal().source(ProgressState.CONTACT)
                .target(ProgressState.OPERATIONS).event(OnboardingEvent.UPDATE_OPERATIONAL_INFO).guard(genericGuard)
                .action(genericAction).and().withExternal().source(ProgressState.OPERATIONS)
                .target(ProgressState.COMPLETED).event(OnboardingEvent.APPROVE).and().withExternal()
                .source(ProgressState.CONTACT).target(ProgressState.CONTACT).event(OnboardingEvent.UPDATE_CONTACT_INFO)
                .guard(genericGuard).action(genericAction).and().withExternal().source(ProgressState.OPERATIONS)
                .target(ProgressState.OPERATIONS).event(OnboardingEvent.UPDATE_OPERATIONAL_INFO).guard(genericGuard)
                .action(genericAction);
    }
}
