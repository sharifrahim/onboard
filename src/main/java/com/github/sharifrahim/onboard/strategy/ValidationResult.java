package com.github.sharifrahim.onboard.strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of validation operations
 */
public class ValidationResult {

    private boolean valid;
    private List<String> errors;

    public ValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
    }

    public ValidationResult(boolean valid) {
        this.valid = valid;
        this.errors = new ArrayList<>();
    }

    public static ValidationResult success() {
        return new ValidationResult(true);
    }

    public static ValidationResult failure(String error) {
        ValidationResult result = new ValidationResult(false);
        result.addError(error);
        return result;
    }

    public static ValidationResult failure(List<String> errors) {
        ValidationResult result = new ValidationResult(false);
        result.errors.addAll(errors);
        return result;
    }

    public void addError(String error) {
        this.valid = false;
        this.errors.add(error);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public String getErrorMessage() {
        return String.join("; ", errors);
    }
}
