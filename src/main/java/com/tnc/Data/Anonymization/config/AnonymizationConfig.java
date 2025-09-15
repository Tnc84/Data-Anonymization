package com.tnc.Data.Anonymization.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
    
    // Getters and Setters
    public String getDefaultStrategy() {
        return defaultStrategy;
    }
    
    public void setDefaultStrategy(String defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }
    
    public boolean isDefaultPreserveFormat() {
        return defaultPreserveFormat;
    }
    
    public void setDefaultPreserveFormat(boolean defaultPreserveFormat) {
        this.defaultPreserveFormat = defaultPreserveFormat;
    }
    
    public int getMaxBatchSize() {
        return maxBatchSize;
    }
    
    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }
    
    public int getCacheMaxSize() {
        return cacheMaxSize;
    }
    
    public void setCacheMaxSize(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }
    
    public boolean isEnableCaching() {
        return enableCaching;
    }
    
    public void setEnableCaching(boolean enableCaching) {
        this.enableCaching = enableCaching;
    }
    
    public List<String> getSensitiveFields() {
        return sensitiveFields;
    }
    
    public void setSensitiveFields(List<String> sensitiveFields) {
        this.sensitiveFields = sensitiveFields;
    }
    
    public Map<String, String> getFieldTypeMapping() {
        return fieldTypeMapping;
    }
    
    public void setFieldTypeMapping(Map<String, String> fieldTypeMapping) {
        this.fieldTypeMapping = fieldTypeMapping;
    }
    
    /**
     * Check if a field is considered sensitive
     */
    public boolean isSensitiveField(String fieldName) {
        if (fieldName == null) return false;
        
        String lowerFieldName = fieldName.toLowerCase();
        return sensitiveFields.stream()
            .anyMatch(sensitiveField -> lowerFieldName.contains(sensitiveField.toLowerCase()));
    }
}
