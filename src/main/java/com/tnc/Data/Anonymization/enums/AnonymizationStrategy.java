package com.tnc.Data.Anonymization.enums;

/**
 * Enumeration of available anonymization strategies.
 * Follows SOLID principles - Open/Closed principle for extensibility.
 */
public enum AnonymizationStrategy {
    
    PSEUDONYMIZATION("Consistent, reversible anonymization using SHA-256 hashing"),
    MASKING("Realistic fake data using JavaFaker library"),
    REDACTION("Complete removal of sensitive information"),
    FORMAT_PRESERVING_ENCRYPTION("Maintains original data format");
    
    private final String description;
    
    AnonymizationStrategy(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Get strategy from string value (case-insensitive)
     */
    public static AnonymizationStrategy fromString(String strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        
        try {
            return AnonymizationStrategy.valueOf(strategy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown anonymization strategy: " + strategy);
        }
    }
}
