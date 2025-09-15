package com.tnc.Data.Anonymization.enums;

/**
 * Enumeration of supported data types for anonymization.
 * Helps in applying appropriate anonymization techniques.
 */
public enum DataType {
    
    NAME("Personal names - first, last, full"),
    EMAIL("Email addresses"),
    PHONE("Phone numbers"),
    ADDRESS("Street addresses, cities, postal codes"),
    SSN("Social Security Numbers"),
    CREDIT_CARD("Credit card numbers"),
    DATE("Date values"),
    NUMBER("Numeric values"),
    TEXT("General text content"),
    ID("Identifier values"),
    BOOLEAN("Boolean values"),
    UNKNOWN("Unknown or unclassified data type");
    
    private final String description;
    
    DataType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Attempts to classify data type based on field name and value
     */
    public static DataType classifyFromFieldName(String fieldName) {
        if (fieldName == null) return UNKNOWN;
        
        String lowerField = fieldName.toLowerCase();
        
        if (lowerField.contains("name") || lowerField.contains("firstname") || lowerField.contains("lastname")) {
            return NAME;
        } else if (lowerField.contains("email") || lowerField.contains("mail")) {
            return EMAIL;
        } else if (lowerField.contains("phone") || lowerField.contains("tel") || lowerField.contains("mobile")) {
            return PHONE;
        } else if (lowerField.contains("address") || lowerField.contains("street") || lowerField.contains("city") || lowerField.contains("zip")) {
            return ADDRESS;
        } else if (lowerField.contains("ssn") || lowerField.contains("social")) {
            return SSN;
        } else if (lowerField.contains("card") || lowerField.contains("credit")) {
            return CREDIT_CARD;
        } else if (lowerField.contains("date") || lowerField.contains("birth") || lowerField.contains("dob")) {
            return DATE;
        } else if (lowerField.contains("id") || lowerField.contains("identifier")) {
            return ID;
        }
        
        return UNKNOWN;
    }
}
