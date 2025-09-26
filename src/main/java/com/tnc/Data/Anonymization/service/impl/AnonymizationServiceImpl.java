package com.tnc.Data.Anonymization.service.impl;

import com.tnc.Data.Anonymization.enums.AnonymizationStrategy;
import com.tnc.Data.Anonymization.enums.DataType;
import com.tnc.Data.Anonymization.dto.AnonymizationRequest;
import com.tnc.Data.Anonymization.dto.AnonymizationResponse;
import com.tnc.Data.Anonymization.service.interfaces.AnonymizationService;
import com.tnc.Data.Anonymization.service.interfaces.DataAnonymizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Main implementation of AnonymizationService.
 * Follows SOLID principles:
 * - Single Responsibility: Orchestrates anonymization process
 * - Open/Closed: Open for extension with new strategies
 * - Dependency Inversion: Depends on DataAnonymizer abstraction
 * - Interface Segregation: Implements focused interface
 */
@Service
public class AnonymizationServiceImpl implements AnonymizationService {
    
    private final DataAnonymizer fakeDataAnonymizer;
    private final DataAnonymizer pseudonymizationAnonymizer;

    public AnonymizationServiceImpl(
            @Qualifier("fakeDataAnonymizer") DataAnonymizer fakeDataAnonymizer,
            @Qualifier("pseudonymizationAnonymizer") DataAnonymizer pseudonymizationAnonymizer) {
        this.fakeDataAnonymizer = fakeDataAnonymizer;
        this.pseudonymizationAnonymizer = pseudonymizationAnonymizer;
    }
    
    @Override
    public AnonymizationResponse anonymizeData(AnonymizationRequest request) {
        try {
            AnonymizationStrategy strategy = AnonymizationStrategy.fromString(request.getStrategy());
            
            Map<String, Object> anonymizedData = anonymizeMap(
                request.getData(), 
                strategy, 
                request.isPreserveFormat(), 
                request.getSeed()
            );
            
            AnonymizationResponse response = new AnonymizationResponse(anonymizedData, strategy.name(), true);
            response.setFieldsProcessed(anonymizedData.size());
            response.setMessage("Data anonymized successfully using " + strategy.getDescription());
            
            return response;
            
        } catch (Exception e) {
            AnonymizationResponse errorResponse = new AnonymizationResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Anonymization failed: " + e.getMessage());
            errorResponse.setStrategy(request.getStrategy());
            return errorResponse;
        }
    }
    
    @Override
    public Map<String, Object> anonymizeMap(Map<String, Object> data, AnonymizationStrategy strategy, 
                                          boolean preserveFormat, Long seed) {
        if (data == null || data.isEmpty()) {
            return new HashMap<>();
        }
        
        DataAnonymizer anonymizer = getAnonymizer(strategy);
        Map<String, Object> anonymizedData = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            
            if (value == null) {
                anonymizedData.put(fieldName, null);
                continue;
            }
            
            Object anonymizedValue;
            
            if (value instanceof Map) {
                // Recursively handle nested objects
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                anonymizedValue = anonymizeMap(nestedMap, strategy, preserveFormat, seed);
            } else if (value instanceof List) {
                // Handle lists
                anonymizedValue = anonymizeList((List<?>) value, strategy, preserveFormat, seed, fieldName);
            } else {
                // Handle primitive values
                anonymizedValue = anonymizer.anonymize(value, fieldName, preserveFormat, seed);
            }
            
            anonymizedData.put(fieldName, anonymizedValue);
        }
        
        return anonymizedData;
    }
    
    @Override
    public AnonymizationStrategy[] getAvailableStrategies() {
        return AnonymizationStrategy.values();
    }
    
    private DataAnonymizer getAnonymizer(AnonymizationStrategy strategy) {
        return switch (strategy) {
            case PSEUDONYMIZATION -> pseudonymizationAnonymizer;
            case MASKING -> fakeDataAnonymizer;
            case REDACTION -> new RedactionAnonymizer();
            case FORMAT_PRESERVING_ENCRYPTION -> pseudonymizationAnonymizer; // Use pseudonymization as FPE alternative
        };
    }
    
    private List<Object> anonymizeList(List<?> list, AnonymizationStrategy strategy, 
                                     boolean preserveFormat, Long seed, String fieldName) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        
        DataAnonymizer anonymizer = getAnonymizer(strategy);
        List<Object> anonymizedList = new ArrayList<>();
        
        for (Object item : list) {
            if (item == null) {
                anonymizedList.add(null);
            } else if (item instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> mapItem = (Map<String, Object>) item;
                anonymizedList.add(anonymizeMap(mapItem, strategy, preserveFormat, seed));
            } else if (item instanceof List) {
                anonymizedList.add(anonymizeList((List<?>) item, strategy, preserveFormat, seed, fieldName));
            } else {
                Object anonymizedItem = anonymizer.anonymize(item, fieldName, preserveFormat, seed);
                anonymizedList.add(anonymizedItem);
            }
        }
        
        
        return anonymizedList;
    }
    
    /**
     * Simple redaction anonymizer for demonstration
     */
    private static class RedactionAnonymizer implements DataAnonymizer {
        
        @Override
        public Object anonymize(Object value, DataType dataType, boolean preserveFormat, Long seed) {
            if (value == null) return null;
            
            String original = value.toString();
            return switch (dataType) {
                case NAME, EMAIL, PHONE, SSN, CREDIT_CARD -> "***REDACTED***";
                case ADDRESS -> "*** ADDRESS REDACTED ***";
                case TEXT -> original.length() > 10 ? "*** TEXT REDACTED ***" : "***";
                case NUMBER -> preserveFormat ? "000" : 0;
                case ID -> "***ID***";
                default -> "***";
            };
        }
        
        @Override
        public Object anonymize(Object value, String fieldName, boolean preserveFormat, Long seed) {
            DataType dataType = DataType.classifyFromFieldName(fieldName);
            return anonymize(value, dataType, preserveFormat, seed);
        }
        
        @Override
        public boolean supports(DataType dataType) {
            return true;
        }
    }
}
