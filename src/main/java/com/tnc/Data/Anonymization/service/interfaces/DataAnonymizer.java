package com.tnc.Data.Anonymization.service.interfaces;

import com.tnc.Data.Anonymization.enums.DataType;

/**
 * Interface for data anonymization operations.
 * Follows SOLID principles:
 * - Single Responsibility: Defines contract for data anonymization
 * - Open/Closed: Open for extension, closed for modification
 * - Interface Segregation: Focused on anonymization operations only
 */
public interface DataAnonymizer {
    
    /**
     * Anonymizes a single value based on its data type
     * 
     * @param value The original value to anonymize
     * @param dataType The type of data being anonymized
     * @param preserveFormat Whether to maintain the original format
     * @param seed Optional seed for consistent anonymization
     * @return Anonymized value
     */
    Object anonymize(Object value, DataType dataType, boolean preserveFormat, Long seed);
    
    /**
     * Anonymizes a single value with auto-detection of data type
     * 
     * @param value The original value to anonymize
     * @param fieldName Field name to help with type detection
     * @param preserveFormat Whether to maintain the original format
     * @param seed Optional seed for consistent anonymization
     * @return Anonymized value
     */
    Object anonymize(Object value, String fieldName, boolean preserveFormat, Long seed);
    
    /**
     * Checks if this anonymizer supports the given data type
     * 
     * @param dataType The data type to check
     * @return true if supported, false otherwise
     */
    boolean supports(DataType dataType);
}
