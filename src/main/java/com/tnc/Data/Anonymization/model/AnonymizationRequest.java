package com.tnc.Data.Anonymization.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request model for data anonymization operations.
 * Follows SOLID principles - Single Responsibility for request data structure.
 */
@Getter
@Setter
@NoArgsConstructor
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
    
    public AnonymizationRequest(Map<String, Object> data, String strategy) {
        this.data = data;
        this.strategy = strategy;
    }
}
