# Company Onboarding System

A Spring Boot application that manages company onboarding workflow using **Spring State Machine** with a **Registry Pattern** for strategy-based validation and execution.

## 🚀 Overview

This application provides a robust onboarding system for companies with multi-step approval processes. It leverages Spring State Machine for workflow management and implements a flexible strategy pattern for handling different onboarding steps.

## 🏗️ Architecture

### Core Components

- **Spring State Machine**: Manages onboarding workflow states and transitions
- **Strategy Registry Pattern**: Flexible strategy selection and execution
- **Approval System**: Tracks all onboarding changes for approval workflow
- **REST API**: RESTful endpoints for onboarding operations

### State Machine Flow

```
PROFILE → CONTACT → OPERATIONS → COMPLETED
    ↓        ↓          ↓
[CREATE]  [UPDATE]   [UPDATE]
COMPANY   CONTACT   OPERATIONS
```

## 🎯 Features

### ✅ **State Management**
- **Progressive Workflow**: Profile → Contact → Operations → Completion
- **State Persistence**: Current progress tracking
- **Event-Driven**: Transitions triggered by business events

### ✅ **Strategy Pattern**
- **Registry-Based**: Dynamic strategy selection
- **Flexible Validation**: Context-aware validation logic
- **Priority Support**: Multiple strategies with priority ordering
- **Easy Extension**: Add new strategies without code changes

### ✅ **Approval Workflow**
- **Change Tracking**: All modifications recorded for approval
- **Audit Trail**: Complete history of onboarding changes
- **Rollback Support**: Restore from approved states

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring State Machine 4.0.0**
- **Spring Data JPA**
- **H2 Database** (development)
- **Flyway** (database migrations)
- **Maven** (build tool)
- **Lombok** (code generation)

## 📁 Project Structure

```
src/main/java/com/github/sharifrahim/onboard/
├── controller/
│   └── CompanyController.java           # REST endpoints
├── domain/
│   ├── Approval.java                    # Approval entity
│   ├── Company.java                     # Company entity
│   └── ProgressState.java               # State enumeration
├── dto/
│   ├── CompanyProfileRequest.java       # Company profile DTO
│   ├── ContactInfoRequest.java          # Contact info DTO
│   └── OperationalInfoRequest.java      # Operations DTO
├── repository/
│   ├── ApprovalRepository.java          # Approval data access
│   └── CompanyRepository.java           # Company data access
├── service/
│   └── ApprovalService.java             # Approval business logic
└── statemachine/
    ├── config/
    │   └── OnboardingStateMachineConfig.java    # State machine configuration
    ├── service/
    │   └── OnboardingStateMachineService.java   # State machine service
    ├── strategy/
    │   ├── OnboardingStateMachineStrategy.java  # Strategy interface
    │   ├── OnboardingStrategyRegistry.java      # Strategy registry
    │   └── impl/
    │       ├── CreateCompanyStateMachineStrategy.java
    │       ├── UpdateContactInfoStateMachineStrategy.java
    │       └── UpdateOperationalInfoStateMachineStrategy.java
    ├── OnboardingEvent.java             # Event enumeration
    └── OnboardingStateMachineImpl.java   # Legacy implementation
```

## 🔧 Configuration

### State Machine States
- `PROFILE` - Company profile creation
- `CONTACT` - Contact information setup  
- `OPERATIONS` - Operational details configuration
- `COMPLETED` - Onboarding finished

### Events
- `CREATE_COMPANY` - Create new company profile
- `UPDATE_CONTACT_INFO` - Update contact information
- `UPDATE_OPERATIONAL_INFO` - Update operational details
- `APPROVE` - Approve and complete onboarding

## 🚦 Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+

### Running the Application

```bash
# Clone the repository
git clone <repository-url>
cd onboard

# Build and run
mvn spring-boot:run
```

### Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## 📡 API Endpoints

### Company Management

```http
POST /companies/profile
Content-Type: application/json

{
  "name": "Tech Corp",
  "registrationNumber": "REG123456",
  "entityType": "CORPORATION",
  "country": "US"
}
```

```http
PUT /companies/{id}/contact
Content-Type: application/json

{
  "mainContactName": "John Doe",
  "mainContactEmail": "john@techcorp.com",
  "mainContactPhone": "+1234567890"
}
```

```http
PUT /companies/{id}/operations
Content-Type: application/json

{
  "taxIdNumber": "TAX123456",
  "bankName": "Bank of America",
  "roleOnPlatform": "CUSTOMER"
}
```

### Approval Management

```http
POST /companies/approvals/{id}/approve
```

```http
POST /companies/approvals/{id}/restore
```

## 🎨 Strategy Pattern Implementation

### Creating Custom Strategies

```java
@Component
public class CustomValidationStrategy implements OnboardingStateMachineStrategy {
    
    @Override
    public boolean validate(StateContext<ProgressState, OnboardingEvent> context) {
        // Custom validation logic
        return true;
    }
    
    @Override
    public void onSuccess(StateContext<ProgressState, OnboardingEvent> context) {
        // Custom success logic
    }
    
    @Override
    public OnboardingEvent getEvent() {
        return OnboardingEvent.CREATE_COMPANY;
    }
    
    @Override
    public boolean canHandle(ProgressState currentState, OnboardingEvent event, 
                           StateContext<ProgressState, OnboardingEvent> context) {
        // Custom handling logic based on state/event/context
        return getEvent().equals(event) && someBusinessRule(context);
    }
    
    @Override
    public int getPriority() {
        return 50; // Higher priority (lower number)
    }
}
```

### Registry Benefits

- **Dynamic Selection**: Strategies selected at runtime based on context
- **Priority Ordering**: Multiple strategies can handle same event with priority
- **Business Rules**: Complex selection logic based on state/event/context
- **Easy Testing**: Mock strategies for unit tests

## 🗄️ Database Schema

### Companies Table
```sql
CREATE TABLE companies (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100),
    entity_type VARCHAR(50),
    progress_state VARCHAR(50),
    -- ... additional fields
);
```

### Approvals Table  
```sql
CREATE TABLE approvals (
    id BIGINT PRIMARY KEY,
    data_id BIGINT,
    data_type VARCHAR(50),
    operation_type VARCHAR(20),
    old_data TEXT,
    new_data TEXT,
    approval_status VARCHAR(20),
    submitted_at TIMESTAMP,
    -- ... additional fields
);
```

## 🔍 Monitoring & Observability

### Actuator Endpoints
- `/actuator/health` - Application health
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics

### H2 Console (Development)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:onboard`
- Username: `sa`
- Password: _(empty)_

## 🧪 Testing Strategy

### Unit Tests
- Strategy implementations
- State machine configuration
- Service layer logic

### Integration Tests  
- Complete onboarding workflows
- State transitions
- Database operations

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For questions and support, please open an issue in the GitHub repository.

---

**Built with ❤️ using Spring Boot and Spring State Machine**