# Data Anonymization Application

A comprehensive Spring Boot application for anonymizing sensitive data using various strategies including pseudonymization, data masking, and redaction.

## 🚀 Features

- **Multiple Anonymization Strategies**:
  - **Pseudonymization**: Consistent, reversible anonymization using SHA-256 hashing
  - **Data Masking**: Realistic fake data using JavaFaker library
  - **Redaction**: Complete removal of sensitive information
  - **Format Preserving Encryption**: Maintains original data format

- **Intelligent Data Type Detection**: Automatically classifies data types (names, emails, phones, addresses, etc.)
- **Format Preservation**: Option to maintain original data formats
- **Batch Processing**: Handle multiple datasets simultaneously
- **RESTful API**: Easy integration with existing systems
- **Configurable**: Customizable anonymization rules and strategies

## 🏗️ Architecture

The application follows SOLID principles with a clean, extensible architecture:

```
├── controller/          # REST API endpoints
├── service/            # Business logic
│   ├── interfaces/     # Service contracts
│   └── impl/          # Service implementations
├── model/             # Request/Response models
├── enums/             # Enumerations
├── config/            # Configuration classes
└── exception/         # Global exception handling
```

## 🛠️ Technologies Used

- **Spring Boot 3.5.5** - Main framework
- **Java 21** - Programming language
- **JavaFaker** - Fake data generation
- **Jackson** - JSON processing
- **H2 Database** - In-memory database for testing
- **Spring Boot Actuator** - Health checks and monitoring

## 📋 API Endpoints

### Anonymize Data
```http
POST /api/v1/anonymization/anonymize
Content-Type: application/json

{
  "data": {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@email.com",
    "phone": "555-123-4567",
    "ssn": "123-45-6789"
  },
  "strategy": "MASKING",
  "preserveFormat": true,
  "seed": 12345
}
```

### Quick Anonymize (Default Settings)
```http
POST /api/v1/anonymization/quick-anonymize
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@email.com"
}
```

### Batch Anonymization
```http
POST /api/v1/anonymization/batch-anonymize
Content-Type: application/json

{
  "datasets": {
    "users": {
      "name": "John Doe",
      "email": "john@example.com"
    },
    "employees": {
      "firstName": "Jane",
      "lastName": "Smith"
    }
  },
  "strategy": "PSEUDONYMIZATION",
  "preserveFormat": true
}
```

### Get Available Strategies
```http
GET /api/v1/anonymization/strategies
```

### Health Check
```http
GET /api/v1/anonymization/health
```

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Running the Application

1. **Clone the repository** (if applicable)
2. **Navigate to the project directory**
3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
4. **Access the application**:
   - API Base URL: `http://localhost:8080/api/v1/anonymization`
   - Health Check: `http://localhost:8080/actuator/health`
   - H2 Console: `http://localhost:8080/h2-console`

### Configuration

Configure the application using `application.properties`:

```properties
# Anonymization Configuration
anonymization.default-strategy=MASKING
anonymization.default-preserve-format=true
anonymization.max-batch-size=1000
anonymization.cache-max-size=10000
anonymization.enable-caching=true

# Server Configuration
server.port=8080
```

## 📊 Anonymization Strategies

### 1. Pseudonymization
- **Use Case**: When you need consistent anonymization that can be reversed
- **Benefits**: Maintains referential integrity, GDPR compliant
- **Example**: "John Doe" → "Amd3kF8x" (always the same for the same input)

### 2. Data Masking
- **Use Case**: Generate realistic fake data for testing/development
- **Benefits**: Maintains data utility, realistic appearance
- **Example**: "John Doe" → "Michael Johnson"

### 3. Redaction
- **Use Case**: Complete removal of sensitive information
- **Benefits**: Maximum privacy protection
- **Example**: "John Doe" → "***REDACTED***"

### 4. Format Preserving Encryption
- **Use Case**: Encrypt data while maintaining original format
- **Benefits**: Preserves data structure for systems that require specific formats
- **Example**: "123-45-6789" → "987-65-4321" (maintains SSN format)

## 🔧 Extending the Application

### Adding New Data Types

1. Add to `DataType` enum:
```java
CUSTOM_TYPE("Description of custom type")
```

2. Implement handling in anonymizers:
```java
case CUSTOM_TYPE -> generateCustomData(value, faker, preserveFormat);
```

### Adding New Anonymization Strategies

1. Add to `AnonymizationStrategy` enum
2. Create new anonymizer implementing `DataAnonymizer`
3. Update `AnonymizationServiceImpl.getAnonymizer()`

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 📝 Example Usage

### Java/Spring Boot Integration
```java
@Autowired
private AnonymizationService anonymizationService;

public void anonymizeUserData(Map<String, Object> userData) {
    AnonymizationRequest request = new AnonymizationRequest(userData, "MASKING");
    request.setPreserveFormat(true);
    
    AnonymizationResponse response = anonymizationService.anonymizeData(request);
    // Use response.getAnonymizedData()
}
```

### cURL Examples
```bash
# Quick anonymization
curl -X POST http://localhost:8080/api/v1/anonymization/quick-anonymize \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'

# Get available strategies
curl http://localhost:8080/api/v1/anonymization/strategies
```

## 🛡️ Security & Compliance

- **GDPR Compliant**: Pseudonymization option supports GDPR requirements
- **Data Protection**: Multiple anonymization levels available
- **Audit Trail**: All operations are logged
- **No Data Storage**: Application doesn't persist sensitive data

## 📈 Performance

- **Caching**: Built-in caching for consistent pseudonymization
- **Batch Processing**: Efficient handling of large datasets
- **Memory Management**: Configurable cache sizes
- **Scalability**: Stateless design for horizontal scaling

## 🤝 Contributing

1. Follow SOLID principles
2. Add comprehensive tests
3. Update documentation
4. Follow existing code style

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Check the API documentation
- Review the configuration options
- Examine the example usage patterns
