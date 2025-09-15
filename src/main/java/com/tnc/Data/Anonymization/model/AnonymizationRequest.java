package com.tnc.Data.Anonymization.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Request model for data anonymization operations.
 * Follows SOLID principles - Single Responsibility for request data structure.
 */
public class AnonymizationRequest {
    
    @NotNull(message = "Data cannot be null")
    @JsonProperty("data")
    private Map<String, Object> data;
    
    @NotBlank(message = "Strategy cannot be blank")
    @JsonProperty("strategy")
    private String strategy; // PSEUDONYMIZATION, MASKING, SHUFFLING
    
    @JsonProperty("preserveFormat")
    private boolean preserveFormat = true;
    
    @JsonProperty("seed")
    private Long seed; // For consistent anonymization
    
    // Constructors
    public AnonymizationRequest() {}
    
    public AnonymizationRequest(Map<String, Object> data, String strategy) {
        this.data = data;
        this.strategy = strategy;
    }
    
    // Getters and Setters
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    public String getStrategy() {
        return strategy;
    }
    
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
    
    public boolean isPreserveFormat() {
        return preserveFormat;
    }
    
    public void setPreserveFormat(boolean preserveFormat) {
        this.preserveFormat = preserveFormat;
    }
    
    public Long getSeed() {
        return seed;
    }
    
    public void setSeed(Long seed) {
        this.seed = seed;
    }
}
