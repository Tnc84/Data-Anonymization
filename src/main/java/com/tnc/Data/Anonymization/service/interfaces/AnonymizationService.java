package com.tnc.Data.Anonymization.service.interfaces;

import com.tnc.Data.Anonymization.enums.AnonymizationStrategy;
import com.tnc.Data.Anonymization.model.AnonymizationRequest;
import com.tnc.Data.Anonymization.model.AnonymizationResponse;
import java.util.Map;

/**
 * Service interface for orchestrating data anonymization operations.
 * Follows SOLID principles:
 * - Single Responsibility: Coordinates anonymization process
 * - Dependency Inversion: Depends on abstractions, not concretions
 */
public interface AnonymizationService {
    
    /**
     * Anonymizes data according to the specified request
     * 
     * @param request The anonymization request containing data and configuration
     * @return Response containing anonymized data and metadata
     */
    AnonymizationResponse anonymizeData(AnonymizationRequest request);
    
    /**
     * Anonymizes a map of data using the specified strategy
     * 
     * @param data The data to anonymize
     * @param strategy The anonymization strategy to use
     * @param preserveFormat Whether to preserve original data formats
     * @param seed Optional seed for consistent results
     * @return Map of anonymized data
     */
    Map<String, Object> anonymizeMap(Map<String, Object> data, AnonymizationStrategy strategy, 
                                   boolean preserveFormat, Long seed);
    
    /**
     * Gets available anonymization strategies
     * 
     * @return Array of supported strategies
     */
    AnonymizationStrategy[] getAvailableStrategies();
}
