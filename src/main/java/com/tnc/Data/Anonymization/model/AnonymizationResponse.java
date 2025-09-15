package com.tnc.Data.Anonymization.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response model for data anonymization operations.
 * Follows SOLID principles - Single Responsibility for response data structure.
 */
public class AnonymizationResponse {
    
    @JsonProperty("anonymizedData")
    private Map<String, Object> anonymizedData;
    
    @JsonProperty("strategy")
    private String strategy;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("fieldsProcessed")
    private int fieldsProcessed;
    
    // Constructors
    public AnonymizationResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public AnonymizationResponse(Map<String, Object> anonymizedData, String strategy, boolean success) {
        this();
        this.anonymizedData = anonymizedData;
        this.strategy = strategy;
        this.success = success;
    }
    
    // Getters and Setters
    public Map<String, Object> getAnonymizedData() {
        return anonymizedData;
    }
    
    public void setAnonymizedData(Map<String, Object> anonymizedData) {
        this.anonymizedData = anonymizedData;
    }
    
    public String getStrategy() {
        return strategy;
    }
    
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getFieldsProcessed() {
        return fieldsProcessed;
    }
    
    public void setFieldsProcessed(int fieldsProcessed) {
        this.fieldsProcessed = fieldsProcessed;
    }
}
