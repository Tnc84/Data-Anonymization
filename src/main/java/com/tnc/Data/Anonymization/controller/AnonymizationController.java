package com.tnc.Data.Anonymization.controller;

import com.tnc.Data.Anonymization.enums.AnonymizationStrategy;
import com.tnc.Data.Anonymization.model.AnonymizationRequest;
import com.tnc.Data.Anonymization.model.AnonymizationResponse;
import com.tnc.Data.Anonymization.service.interfaces.AnonymizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for data anonymization operations.
 * Follows SOLID principles:
 * - Single Responsibility: Handles HTTP requests for anonymization
 * - Dependency Inversion: Depends on AnonymizationService abstraction
 */
@RestController
@RequestMapping("/api/v1/anonymization")
@CrossOrigin(origins = "*")
public class AnonymizationController {
    
    private final AnonymizationService anonymizationService;
    
    public AnonymizationController(AnonymizationService anonymizationService) {
        this.anonymizationService = anonymizationService;
    }
    
    /**
     * Anonymize data using the specified strategy
     */
    @PostMapping("/anonymize")
    public ResponseEntity<AnonymizationResponse> anonymizeData(@Valid @RequestBody AnonymizationRequest request) {
        try {
            AnonymizationResponse response = anonymizationService.anonymizeData(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            AnonymizationResponse errorResponse = new AnonymizationResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Quick anonymization with default settings
     */
    @PostMapping("/quick-anonymize")
    public ResponseEntity<Map<String, Object>> quickAnonymize(@RequestBody Map<String, Object> data) {
        try {
            AnonymizationRequest request = new AnonymizationRequest(data, "MASKING");
            request.setPreserveFormat(true);
            
            AnonymizationResponse response = anonymizationService.anonymizeData(request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", response.isSuccess());
            result.put("data", response.getAnonymizedData());
            result.put("message", response.getMessage());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "Quick anonymization failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
    
    /**
     * Get available anonymization strategies
     */
    @GetMapping("/strategies")
    public ResponseEntity<Map<String, Object>> getAvailableStrategies() {
        try {
            AnonymizationStrategy[] strategies = anonymizationService.getAvailableStrategies();
            
            Map<String, Object> response = new HashMap<>();
            response.put("strategies", Arrays.stream(strategies)
                .map(strategy -> Map.of(
                    "name", strategy.name(),
                    "description", strategy.getDescription()
                ))
                .toArray());
            response.put("count", strategies.length);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve strategies: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Data Anonymization Service");
        health.put("version", "1.0.0");
        health.put("availableStrategies", anonymizationService.getAvailableStrategies().length);
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Batch anonymization endpoint for processing multiple datasets
     */
    @PostMapping("/batch-anonymize")
    public ResponseEntity<Map<String, Object>> batchAnonymize(@RequestBody Map<String, Object> batchRequest) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> datasets = (Map<String, Map<String, Object>>) batchRequest.get("datasets");
            String strategy = (String) batchRequest.getOrDefault("strategy", "MASKING");
            Boolean preserveFormat = (Boolean) batchRequest.getOrDefault("preserveFormat", true);
            Long seed = batchRequest.get("seed") != null ? ((Number) batchRequest.get("seed")).longValue() : null;
            
            Map<String, Object> results = new HashMap<>();
            Map<String, Object> anonymizedDatasets = new HashMap<>();
            int totalFieldsProcessed = 0;
            boolean allSuccessful = true;
            
            for (Map.Entry<String, Map<String, Object>> entry : datasets.entrySet()) {
                String datasetName = entry.getKey();
                Map<String, Object> dataset = entry.getValue();
                
                AnonymizationRequest request = new AnonymizationRequest(dataset, strategy);
                request.setPreserveFormat(preserveFormat);
                request.setSeed(seed);
                
                AnonymizationResponse response = anonymizationService.anonymizeData(request);
                
                if (response.isSuccess()) {
                    anonymizedDatasets.put(datasetName, response.getAnonymizedData());
                    totalFieldsProcessed += response.getFieldsProcessed();
                } else {
                    allSuccessful = false;
                    anonymizedDatasets.put(datasetName, Map.of("error", response.getMessage()));
                }
            }
            
            results.put("success", allSuccessful);
            results.put("datasets", anonymizedDatasets);
            results.put("totalFieldsProcessed", totalFieldsProcessed);
            results.put("strategy", strategy);
            results.put("message", allSuccessful ? "Batch anonymization completed successfully" : 
                                                  "Batch anonymization completed with some errors");
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Batch anonymization failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
