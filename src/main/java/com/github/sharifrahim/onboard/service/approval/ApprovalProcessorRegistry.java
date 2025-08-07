package com.github.sharifrahim.onboard.service.approval;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.sharifrahim.onboard.domain.Approval;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Registry for managing approval processors
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApprovalProcessorRegistry {

    private final List<ApprovalProcessor> processors;

    /**
     * Find the appropriate processor for the given approval type
     *
     * @param type
     *            the approval type
     *
     * @return the processor, or empty if none found
     */
    public Optional<ApprovalProcessor> findProcessor(Approval.Type type) {
        log.debug("Finding processor for approval type: {}", type);

        Optional<ApprovalProcessor> processor = processors.stream().filter(p -> p.canHandle(type)).findFirst();

        if (processor.isPresent()) {
            log.debug("Found processor: {} for type: {}", processor.get().getClass().getSimpleName(), type);
        } else {
            log.warn("No processor found for approval type: {}", type);
        }

        return processor;
    }

    /**
     * Get all available processors
     *
     * @return list of all registered processors
     */
    public List<ApprovalProcessor> getAllProcessors() {
        return List.copyOf(processors);
    }

    /**
     * Get all supported approval types
     *
     * @return array of supported approval types
     */
    public Approval.Type[] getSupportedTypes() {
        return processors.stream().map(ApprovalProcessor::getType).distinct().toArray(Approval.Type[]::new);
    }
}
