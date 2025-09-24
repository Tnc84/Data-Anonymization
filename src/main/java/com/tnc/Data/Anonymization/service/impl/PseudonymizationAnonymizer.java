package com.tnc.Data.Anonymization.service.impl;

import com.tnc.Data.Anonymization.enums.DataType;
import com.tnc.Data.Anonymization.service.interfaces.DataAnonymizer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of DataAnonymizer using pseudonymization technique.
 * Provides consistent, reversible anonymization using hashing.
 * Follows SOLID principles:
 * - Single Responsibility: Handles pseudonymization logic
 * - Open/Closed: Can be extended for different hashing algorithms
 */
@Component("pseudonymizationAnonymizer")
public class PseudonymizationAnonymizer implements DataAnonymizer {
    
    private final ConcurrentHashMap<String, String> pseudonymCache = new ConcurrentHashMap<>();
    private final MessageDigest messageDigest;
    
    public PseudonymizationAnonymizer() {
        try {
            this.messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    @Override
    public Object anonymize(Object value, DataType dataType, boolean preserveFormat, Long seed) {
        if (value == null) {
            return null;
        }
        
        String originalValue = value.toString();
        String seedString = seed != null ? seed.toString() : "default";
        String cacheKey = originalValue + "|" + dataType + "|" + seedString;
        
        return pseudonymCache.computeIfAbsent(cacheKey, k -> generatePseudonym(originalValue, dataType, preserveFormat, seedString));
    }
    
    @Override
    public Object anonymize(Object value, String fieldName, boolean preserveFormat, Long seed) {
        DataType dataType = DataType.classifyFromFieldName(fieldName);
        return anonymize(value, dataType, preserveFormat, seed);
    }
    
    @Override
    public boolean supports(DataType dataType) {
        return true; // Pseudonymization can be applied to any data type
    }
    
    private String generatePseudonym(String originalValue, DataType dataType, boolean preserveFormat, String seed) {
        String input = originalValue + "|" + dataType + "|" + seed;
        
        synchronized (messageDigest) {
            byte[] hash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            String base64Hash = Base64.getEncoder().encodeToString(hash);
            
            if (preserveFormat) {
                return formatPseudonym(base64Hash, originalValue, dataType);
            } else {
                return base64Hash.substring(0, Math.min(base64Hash.length(), 16));
            }
        }
    }
    
    private String formatPseudonym(String hash, String originalValue, DataType dataType) {
        return switch (dataType) {
            case NAME -> formatName(hash, originalValue);
            case EMAIL -> formatEmail(hash, originalValue);
            case PHONE -> formatPhone(hash, originalValue);
            case SSN -> formatSSN(hash);
            case CREDIT_CARD -> formatCreditCard(hash, originalValue);
            case NUMBER -> formatNumber(hash, originalValue);
            case ID -> formatId(hash, originalValue);
            default -> hash.substring(0, Math.min(hash.length(), originalValue.length()));
        };
    }
    
    private String formatName(String hash, String originalValue) {
        // Preserve capitalization pattern
        StringBuilder result = new StringBuilder();
        int hashIndex = 0;
        
        for (int i = 0; i < originalValue.length() && hashIndex < hash.length(); i++) {
            char originalChar = originalValue.charAt(i);
            char hashChar = hash.charAt(hashIndex % hash.length());
            
            if (Character.isLetter(originalChar)) {
                if (Character.isUpperCase(originalChar)) {
                    result.append(Character.toUpperCase(Character.isLetter(hashChar) ? hashChar : 'A'));
                } else {
                    result.append(Character.toLowerCase(Character.isLetter(hashChar) ? hashChar : 'a'));
                }
                hashIndex++;
            } else {
                result.append(originalChar); // Preserve spaces, punctuation
            }
        }
        
        return result.toString();
    }
    
    private String formatEmail(String hash, String originalValue) {
        if (originalValue.contains("@")) {
            String[] parts = originalValue.split("@");
            String domain = parts.length > 1 ? "@" + parts[1] : "@example.com";
            String username = hash.replaceAll("[^a-zA-Z0-9]", "").substring(0, Math.min(8, hash.length()));
            return username.toLowerCase() + domain;
        }
        return hash.replaceAll("[^a-zA-Z0-9]", "").substring(0, Math.min(8, hash.length())) + "@example.com";
    }
    
    private String formatPhone(String hash, String originalValue) {
        StringBuilder result = new StringBuilder();
        String digits = hash.replaceAll("[^0-9]", "");
        if (digits.length() < 10) {
            digits = (digits + "1234567890").substring(0, 10);
        }
        
        int digitIndex = 0;
        for (char c : originalValue.toCharArray()) {
            if (Character.isDigit(c) && digitIndex < digits.length()) {
                result.append(digits.charAt(digitIndex++));
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    private String formatSSN(String hash) {
        String digits = hash.replaceAll("[^0-9]", "");
        if (digits.length() < 9) {
            digits = (digits + "123456789").substring(0, 9);
        }
        return digits.substring(0, 3) + "-" + digits.substring(3, 5) + "-" + digits.substring(5, 9);
    }
    
    private String formatCreditCard(String hash, String originalValue) {
        String digits = hash.replaceAll("[^0-9]", "");
        if (digits.length() < 16) {
            digits = (digits + "1234567890123456").substring(0, 16);
        }
        
        if (originalValue.contains("-")) {
            return digits.substring(0, 4) + "-" + digits.substring(4, 8) + "-" + 
                   digits.substring(8, 12) + "-" + digits.substring(12, 16);
        } else if (originalValue.contains(" ")) {
            return digits.substring(0, 4) + " " + digits.substring(4, 8) + " " + 
                   digits.substring(8, 12) + " " + digits.substring(12, 16);
        }
        return digits.substring(0, 16);
    }
    
    private String formatNumber(String hash, String originalValue) {
        String digits = hash.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            digits = "123";
        }
        
        int targetLength = Math.min(originalValue.length(), 10);
        if (digits.length() < targetLength) {
            digits = (digits + "1234567890").substring(0, targetLength);
        }
        
        return digits.substring(0, Math.min(digits.length(), targetLength));
    }
    
    private String formatId(String hash, String originalValue) {
        if (originalValue.matches("\\d+")) {
            // Numeric ID
            return formatNumber(hash, originalValue);
        } else {
            // Alphanumeric ID - preserve pattern
            StringBuilder result = new StringBuilder();
            int hashIndex = 0;
            
            for (char c : originalValue.toCharArray()) {
                if (Character.isDigit(c)) {
                    String digits = hash.replaceAll("[^0-9]", "");
                    if (!digits.isEmpty()) {
                        result.append(digits.charAt(hashIndex % digits.length()));
                    } else {
                        result.append('1');
                    }
                } else if (Character.isLetter(c)) {
                    String letters = hash.replaceAll("[^a-zA-Z]", "");
                    if (!letters.isEmpty()) {
                        char letter = letters.charAt(hashIndex % letters.length());
                        result.append(Character.isUpperCase(c) ? Character.toUpperCase(letter) : Character.toLowerCase(letter));
                    } else {
                        result.append(Character.isUpperCase(c) ? 'A' : 'a');
                    }
                } else {
                    result.append(c);
                }
                hashIndex++;
            }
            
            return result.toString();
        }
    }
    
    /**
     * Clears the pseudonym cache (useful for testing or memory management)
     */
    public void clearCache() {
        pseudonymCache.clear();
    }
    
    /**
     * Gets the current cache size
     */
    public int getCacheSize() {
        return pseudonymCache.size();
    }
}
