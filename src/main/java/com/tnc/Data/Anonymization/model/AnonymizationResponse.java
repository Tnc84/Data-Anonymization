package com.tnc.Data.Anonymization.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response model for data anonymization operations.
 * Follows SOLID principles - Single Responsibility for response data structure.
 */
@Getter
@Setter
@NoArgsConstructor
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
    
    public AnonymizationResponse(Map<String, Object> anonymizedData, String strategy, boolean success) {
        this.timestamp = LocalDateTime.now();
        this.anonymizedData = anonymizedData;
        this.strategy = strategy;
        this.success = success;
    }
}
