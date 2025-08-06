# State Machine Implementation for Company Onboarding

## Overview

This implementation introduces a state machine pattern with strategy interfaces to handle the company onboarding process. The pattern separates concerns by moving validation and business logic from the controller into dedicated strategy classes.

## Architecture

### Core Components

1. **OnboardingStateMachine**: Interface defining the state machine contract
2. **OnboardingStateMachineImpl**: Implementation that orchestrates the validation and execution flow
3. **OnboardingStrategy<T>**: Generic strategy interface with validate() and onSuccess() methods
4. **OnboardingEvent**: Enum defining the events that trigger state transitions
5. **ValidationResult**: Result object for validation operations
6. **OnboardingStrategyFactory**: Factory for creating strategy instances

### Strategy Pattern Implementation

Each onboarding operation is implemented as a strategy:

- **CreateCompanyStrategy**: Handles new company profile creation
- **UpdateContactInfoStrategy**: Handles contact information updates
- **UpdateOperationalInfoStrategy**: Handles operational information updates

### State Machine Flow

1. **Guard Phase**: The `validate()` method checks business rules and state transitions
2. **Action Phase**: The `onSuccess()` method executes the business logic if validation passes
3. **Approval Creation**: The state machine creates an approval record for the operation

## Key Benefits

### Separation of Concerns
- Controller handles HTTP concerns only
- Strategies contain business logic and validation
- State machine orchestrates the flow

### Extensibility
- Easy to add new onboarding steps by implementing new strategies
- State transitions are clearly defined and enforced
- Validation logic is centralized per operation type

### Maintainability
- Each strategy is focused on a single responsibility
- Business rules are isolated and testable
- Clear separation between validation and execution logic

## Usage Examples

### Creating a Company Profile
```java
@PostMapping("/profile")
public ResponseEntity<Long> createCompany(@Valid @RequestBody CompanyProfileRequest request) {
    try {
        Long approvalId = stateMachine.submitEvent(
            OnboardingEvent.CREATE_COMPANY,
            strategyFactory.getStrategy(OnboardingEvent.CREATE_COMPANY),
            request,
            null
        );
        return new ResponseEntity<>(approvalId, HttpStatus.CREATED);
    } catch (ValidationException e) {
        return ResponseEntity.badRequest().build();
    }
}
```

### Updating Contact Information
```java
@PutMapping("/{id}/contact")
public ResponseEntity<Long> updateContactInfo(@PathVariable Long id,
        @Valid @RequestBody ContactInfoRequest request) {
    Optional<Company> optional = companyRepository.findById(id);
    if (optional.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    
    try {
        Long approvalId = stateMachine.submitEvent(
            OnboardingEvent.UPDATE_CONTACT_INFO,
            strategyFactory.getStrategy(OnboardingEvent.UPDATE_CONTACT_INFO),
            request,
            optional.get()
        );
        return ResponseEntity.ok(approvalId);
    } catch (ValidationException e) {
        return ResponseEntity.badRequest().build();
    }
}
```

## Validation Rules

### Create Company Strategy
- Company name is required
- Registration number is required
- Entity type is required
- Country is required
- Company must not already exist

### Update Contact Info Strategy
- Company must exist
- Must be in PROFILE or CONTACT state
- Main contact name, email, and phone are required
- Contact person role is required
- Email format validation

### Update Operational Info Strategy
- Company must exist
- Must be in CONTACT or OPERATIONS state
- Tax ID, bank information, and payment method are required
- Role on platform and operating hours are required
- Terms of service agreement validation

## State Transitions

The onboarding process follows these state transitions:

1. **PROFILE** → **CONTACT** (when contact info is updated)
2. **CONTACT** → **OPERATIONS** (when operational info is updated)
3. **OPERATIONS** → **COMPLETED** (when approval process is completed)

## Error Handling

The implementation uses a custom `ValidationException` to handle validation failures. The controller catches these exceptions and returns appropriate HTTP error responses.

## Testing

The state machine and strategies are designed to be easily testable. Mock objects can be used to test validation logic and state transitions independently.

## Future Enhancements

1. **Event Sourcing**: Track all state changes as events
2. **Workflow Engine**: Integration with external workflow systems
3. **Notification System**: Automated notifications for state changes
4. **Audit Trail**: Comprehensive logging of all operations
5. **Dynamic Validation**: Configuration-driven validation rules
