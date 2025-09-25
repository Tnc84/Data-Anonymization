package com.tnc.Data.Anonymization.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response model for file-based anonymization operations.
 * Follows SOLID principles - Single Responsibility for file response data structure.
 */
@Getter
@Setter
@NoArgsConstructor
public class FileAnonymizationResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("originalFileName")
    private String originalFileName;
    
    @JsonProperty("anonymizedFileName")
    private String anonymizedFileName;
    
    @JsonProperty("filePath")
    private String filePath;
    
    @JsonProperty("strategy")
    private String strategy;
    
    @JsonProperty("recordsProcessed")
    private int recordsProcessed;
    
    @JsonProperty("fieldsProcessed")
    private int fieldsProcessed;
    
    @JsonProperty("fileSize")
    private long fileSize;
    
    @JsonProperty("downloadUrl")
    private String downloadUrl;
    
    public FileAnonymizationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public FileAnonymizationResponse(String originalFileName, String anonymizedFileName, 
                                   String strategy, int recordsProcessed, int fieldsProcessed) {
        this.success = true;
        this.originalFileName = originalFileName;
        this.anonymizedFileName = anonymizedFileName;
        this.strategy = strategy;
        this.recordsProcessed = recordsProcessed;
        this.fieldsProcessed = fieldsProcessed;
        this.message = "File anonymized successfully";
    }
}
