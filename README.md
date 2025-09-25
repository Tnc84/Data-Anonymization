# Data Anonymization API

A comprehensive Spring Boot application for anonymizing sensitive data using various strategies including pseudonymization, data masking, and redaction. Built with modern Java practices, Lombok for clean code, and comprehensive OpenAPI documentation.

## 🚀 Features

### Core Anonymization
- **Multiple Anonymization Strategies**:
  - **Pseudonymization**: Consistent, reversible anonymization using SHA-256 hashing
  - **Data Masking**: Realistic fake data using JavaFaker library
  - **Redaction**: Complete removal of sensitive information
  - **Format Preserving Encryption**: Maintains original data format

- **Intelligent Data Type Detection**: Automatically classifies data types (names, emails, phones, addresses, etc.)
- **Format Preservation**: Option to maintain original data formats
- **Batch Processing**: Handle multiple datasets simultaneously
- **Nested Data Support**: Recursively processes complex JSON structures

### File Processing ✨ **NEW!**
- **📁 File Upload & Anonymization**: Upload CSV/JSON files for automatic anonymization
- **📂 Organized File Structure**: Anonymized files saved in dedicated `anonymized-files` folder
- **🏷️ Smart File Naming**: Automatic "_anon" suffix (e.g., `test.csv` → `test_anon.csv`)
- **📥 File Download**: Direct download of anonymized files
- **📋 File Management**: List and manage all anonymized files
- **🔄 Format Support**: CSV and JSON file formats supported

### Developer Experience
- **📚 OpenAPI Documentation**: Interactive Swagger UI for API exploration (✅ **WORKING!**)
- **🎯 Lombok Integration**: Clean, boilerplate-free code
- **🔧 RESTful API**: Easy integration with existing systems
- **⚙️ Configurable**: Customizable anonymization rules and strategies
- **🏥 Health Checks**: Built-in monitoring with Spring Boot Actuator

## 🏗️ Architecture

The application follows SOLID principles with a clean, extensible architecture:

```
├── controller/          # REST API endpoints with OpenAPI annotations
├── service/            # Business logic
│   ├── interfaces/     # Service contracts
│   └── impl/          # Service implementations
├── model/             # Request/Response models (Lombok-enhanced)
├── config/            # Configuration classes (OpenAPI, Anonymization)
├── enums/             # Enumerations
└── exception/         # Global exception handling
```

## 🛠️ Technologies Used

- **Spring Boot 3.5.5** - Main framework
- **Java 21** - Programming language
- **Lombok 1.18.36** - Boilerplate code reduction
- **SpringDoc OpenAPI 2.2.0** - API documentation (✅ Working!)
- **JavaFaker 1.0.1** - Fake data generation
- **Jackson 2.20.0** - JSON processing
- **H2 Database** - In-memory database for testing
- **Spring Boot Actuator** - Health checks and monitoring
- **Maven** - Build tool

## 📚 API Documentation ✅ **FULLY WORKING!**

### Interactive Documentation
- **🎉 Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) ✅
- **📄 OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) ✅
- **📄 OpenAPI YAML**: [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml) ✅

### 🔧 **Recent Fixes Applied:**
- ✅ **SpringDoc OpenAPI 2.2.0** - Compatible version for Spring Boot 3.5.5
- ✅ **GlobalExceptionHandler Disabled** - Resolved compatibility conflicts
- ✅ **Lombok Integration** - Clean, boilerplate-free code
- ✅ **Full API Testing** - All endpoints working via Swagger UI

### Key Endpoints

#### File Anonymization ✨ **NEW!**

##### Upload and Anonymize File
```http
POST /api/v1/anonymization/upload-anonymize
Content-Type: multipart/form-data

Form Data:
- file: [CSV or JSON file]
- strategy: "MASKING" (optional, default: "MASKING")
- preserveFormat: true (optional, default: true)
- seed: 12345 (optional)
- outputFileName: "custom_name_anon.csv" (optional)
```

##### Download Anonymized File
```http
GET /api/v1/anonymization/download/{fileName}
```

##### List All Anonymized Files
```http
GET /api/v1/anonymization/files
```

#### Data Anonymization

##### Anonymize Data
```http
POST /api/v1/anonymization/anonymize
Content-Type: application/json

{
  "data": {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@email.com",
    "phone": "555-123-4567",
    "ssn": "123-45-6789",
    "address": {
      "street": "123 Main St",
      "city": "Anytown",
      "zipCode": "12345"
    }
  },
  "strategy": "MASKING",
  "preserveFormat": true,
  "seed": 12345
}
```

#### Quick Anonymize (Default Settings)
```http
POST /api/v1/anonymization/quick-anonymize
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@email.com"
}
```

#### Batch Anonymization
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
      "lastName": "Smith",
      "department": "Engineering"
    }
  },
  "strategy": "PSEUDONYMIZATION",
  "preserveFormat": true,
  "seed": 98765
}
```

#### Get Available Strategies
```http
GET /api/v1/anonymization/strategies
```

#### Health Check
```http
GET /api/v1/anonymization/health
```

## 🚀 Getting Started

### Prerequisites
- **Java 21** or higher
- **Maven 3.6+**

### Running the Application

1. **Clone the repository** (if applicable)
2. **Navigate to the project directory**
3. **Run the application**:
   ```bash
   # Using Maven wrapper (recommended)
   ./mvnw spring-boot:run
   
   # Or on Windows
   mvnw.cmd spring-boot:run
   ```

4. **Access the application**:
   - **🎉 Swagger UI**: `http://localhost:8080/swagger-ui/index.html` ✅ **WORKING!**
   - **API Base URL**: `http://localhost:8080/api/v1/anonymization`
   - **Health Check**: `http://localhost:8080/actuator/health`
   - **H2 Console**: `http://localhost:8080/h2-console`

### Configuration

Configure the application using `application.properties`:

```properties
# Server Configuration
server.port=8080

# Anonymization Configuration
anonymization.default-strategy=MASKING
anonymization.default-preserve-format=true
anonymization.max-batch-size=1000
anonymization.cache-max-size=10000
anonymization.enable-caching=true

# OpenAPI Documentation
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## 📊 Anonymization Strategies

### 1. Pseudonymization (`PSEUDONYMIZATION`)
- **Use Case**: When you need consistent anonymization that maintains referential integrity
- **Benefits**: GDPR compliant, reversible with proper key management
- **Caching**: Results are cached for consistency
- **Example**: 
  - Input: `"John Doe"`
  - Output: `"Amd3kF8x"` (always the same for the same input + seed)

### 2. Data Masking (`MASKING`)
- **Use Case**: Generate realistic fake data for testing/development
- **Benefits**: Maintains data utility, realistic appearance
- **Powered by**: JavaFaker library
- **Example**:
  - Input: `"John Doe"`
  - Output: `"Michael Johnson"`

### 3. Redaction (`REDACTION`)
- **Use Case**: Complete removal of sensitive information
- **Benefits**: Maximum privacy protection, simple implementation
- **Example**:
  - Input: `"John Doe"`
  - Output: `"***REDACTED***"`

### 4. Format Preserving Encryption (`FORMAT_PRESERVING_ENCRYPTION`)
- **Use Case**: Encrypt data while maintaining original format
- **Benefits**: Preserves data structure for systems requiring specific formats
- **Implementation**: Currently uses pseudonymization with format preservation
- **Example**:
  - Input: `"123-45-6789"`
  - Output: `"987-65-4321"` (maintains SSN format)

## 🔧 Development Features

### Lombok Integration
The codebase uses Lombok annotations for clean, maintainable code:

```java
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class AnonymizationRequest {
    @NotNull(message = "Data cannot be null")
    private Map<String, Object> data;
    
    @NotBlank(message = "Strategy cannot be blank")
    private String strategy;
    
    private boolean preserveFormat = true;
    private Long seed;
    
    // Constructor with required fields
    public AnonymizationRequest(Map<String, Object> data, String strategy) {
        this.data = data;
        this.strategy = strategy;
    }
}
```

### OpenAPI Annotations
Controllers are fully documented with OpenAPI annotations:

```java
@Operation(
    summary = "Anonymize sensitive data",
    description = "Anonymizes the provided data using the specified strategy"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Data anonymized successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
@PostMapping("/anonymize")
public ResponseEntity<AnonymizationResponse> anonymizeData(
    @Parameter(description = "Anonymization request", required = true)
    @Valid @RequestBody AnonymizationRequest request) {
    // Implementation
}
```

## 🧪 Testing

### Run Tests
```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Skip tests during build
./mvnw clean install -DskipTests
```

### Test Data
Sample test files are provided in the `test-data/` directory:
- `sample-sensitive-data.json` - Complex nested data structure
- `simple-test-data.json` - Basic test data
- `test.csv` - Sample CSV file for file anonymization testing ✨ **NEW!**
- `test-anonymization.bat` / `test-anonymization.sh` - Test scripts for API testing
- `test-file-anonymization.bat` / `test-file-anonymization.sh` - Test scripts for file anonymization ✨ **NEW!**

## 📝 Usage Examples

### ✅ Using Swagger UI (WORKING!)
1. Navigate to `http://localhost:8080/swagger-ui/index.html`
2. Explore available endpoints with full OpenAPI documentation
3. Test APIs directly from the browser - **All endpoints tested successfully!**
4. View request/response schemas with comprehensive examples
5. **Real-time testing confirmed** - API calls return proper anonymized data

### cURL Examples

#### File Anonymization ✨ **NEW!**
```bash
# Upload and anonymize a CSV file
curl -X POST http://localhost:8080/api/v1/anonymization/upload-anonymize \
  -F "file=@test-data/test.csv" \
  -F "strategy=MASKING" \
  -F "preserveFormat=true"

# List all anonymized files
curl http://localhost:8080/api/v1/anonymization/files

# Download an anonymized file
curl -O http://localhost:8080/api/v1/anonymization/download/test_anon.csv
```

#### Data Anonymization
```bash
# Quick anonymization
curl -X POST http://localhost:8080/api/v1/anonymization/quick-anonymize \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","phone":"555-1234"}'

# Get available strategies
curl http://localhost:8080/api/v1/anonymization/strategies

# Health check
curl http://localhost:8080/api/v1/anonymization/health

# Complex anonymization with nested data
curl -X POST http://localhost:8080/api/v1/anonymization/anonymize \
  -H "Content-Type: application/json" \
  -d '{
    "data": {
      "user": {
        "name": "John Doe",
        "contact": {
          "email": "john@example.com",
          "phone": "555-1234"
        }
      }
    },
    "strategy": "PSEUDONYMIZATION",
    "preserveFormat": true,
    "seed": 12345
  }'
```

### Java Integration
```java
@RestController
public class MyController {
    
    @Autowired
    private AnonymizationService anonymizationService;
    
    public void anonymizeUserData(Map<String, Object> userData) {
        AnonymizationRequest request = new AnonymizationRequest(userData, "MASKING");
        request.setPreserveFormat(true);
        request.setSeed(12345L);
        
        AnonymizationResponse response = anonymizationService.anonymizeData(request);
        
        if (response.isSuccess()) {
            Map<String, Object> anonymizedData = response.getAnonymizedData();
            // Use anonymized data
        }
    }
}
```

## 🔧 Extending the Application

### Adding New Data Types

1. **Add to `DataType` enum**:
```java
CUSTOM_TYPE("Description of custom type")
```

2. **Implement handling in anonymizers**:
```java
case CUSTOM_TYPE -> generateCustomData(value, faker, preserveFormat);
```

### Adding New Anonymization Strategies

1. **Add to `AnonymizationStrategy` enum**
2. **Create new anonymizer implementing `DataAnonymizer`**
3. **Update `AnonymizationServiceImpl.getAnonymizer()`**
4. **Add OpenAPI documentation**

### Custom Configuration

```java
@Configuration
@ConfigurationProperties(prefix = "my-custom")
@Getter
@Setter
public class CustomAnonymizationConfig {
    private List<String> customSensitiveFields;
    private Map<String, String> customFieldMappings;
}
```

## 🛡️ Security & Compliance

- **GDPR Compliant**: Pseudonymization option supports GDPR requirements
- **Data Protection**: Multiple anonymization levels available
- **Audit Trail**: All operations are logged with Spring Boot logging
- **No Data Persistence**: Application doesn't store sensitive data
- **Input Validation**: Comprehensive validation using Bean Validation
- **Error Handling**: Global exception handling prevents data leakage

## 📈 Performance & Scalability

- **Caching**: Built-in caching for consistent pseudonymization results
- **Batch Processing**: Efficient handling of large datasets
- **Memory Management**: Configurable cache sizes and limits
- **Stateless Design**: Horizontal scaling ready
- **Async Support**: Can be extended for asynchronous processing

## 🐳 Deployment

### Docker Support
```dockerfile
FROM openjdk:21-jre-slim
COPY target/Data-Anonymization-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
```bash
export ANONYMIZATION_DEFAULT_STRATEGY=MASKING
export ANONYMIZATION_CACHE_MAX_SIZE=10000
export SERVER_PORT=8080
```

## 🤝 Contributing

1. **Follow SOLID principles**
2. **Use Lombok annotations appropriately**
3. **Add OpenAPI documentation for new endpoints**
4. **Write comprehensive tests**
5. **Update documentation**
6. **Follow existing code style**

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- **API Documentation**: Visit the Swagger UI at `/swagger-ui/index.html`
- **Configuration**: Review `application.properties` options
- **Examples**: Check the `test-data/` directory
- **Health Check**: Monitor via `/actuator/health`

---

**Built with ❤️ using Spring Boot, Lombok, and OpenAPI**