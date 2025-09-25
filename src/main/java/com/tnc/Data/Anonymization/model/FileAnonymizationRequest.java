package com.tnc.Data.Anonymization.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request model for file-based anonymization operations.
 * Follows SOLID principles - Single Responsibility for file request data structure.
 */
@Getter
@Setter
@NoArgsConstructor
public class FileAnonymizationRequest {
    
    @NotNull(message = "File cannot be null")
    private MultipartFile file;
    
    @NotBlank(message = "Strategy cannot be blank")
    @JsonProperty("strategy")
    private String strategy = "MASKING"; // Default strategy
    
    @JsonProperty("preserveFormat")
    private boolean preserveFormat = true;
    
    @JsonProperty("seed")
    private Long seed; // For consistent anonymization
    
    @JsonProperty("outputFileName")
    private String outputFileName; // Optional custom output filename
    
    public FileAnonymizationRequest(MultipartFile file, String strategy) {
        this.file = file;
        this.strategy = strategy;
    }
}
