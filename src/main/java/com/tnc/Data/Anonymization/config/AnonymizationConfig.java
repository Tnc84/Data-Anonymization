package com.tnc.Data.Anonymization.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Configuration class for anonymization settings.
 * Follows SOLID principles:
 * - Single Responsibility: Manages configuration properties
 * - Open/Closed: Open for extension with new configuration options
 */
@Configuration
@ConfigurationProperties(prefix = "anonymization")
@Getter
@Setter
public class AnonymizationConfig {
    
    private String defaultStrategy = "MASKING";
    private boolean defaultPreserveFormat = true;
    private int maxBatchSize = 1000;
    private int cacheMaxSize = 10000;
    private boolean enableCaching = true;
    private List<String> sensitiveFields = List.of(
        "name", "firstName", "lastName", "email", "phone", "ssn", 
        "creditCard", "address", "dateOfBirth", "password"
    );
    private Map<String, String> fieldTypeMapping;
    
    /**
     * Check if a field is considered sensitive
     */
    public boolean isSensitiveField(String fieldName) {
        if (fieldName == null) return false;
        
        String lowerFieldName = fieldName.toLowerCase();
        return sensitiveFields.stream()
            .anyMatch(sensitiveField -> lowerFieldName.contains(sensitiveField.toLowerCase()));
    }
    
    /**
     * Configure ObjectMapper for JSON processing with LocalDateTime support
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        return mapper;
    }
}
