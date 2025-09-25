package com.tnc.Data.Anonymization.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnc.Data.Anonymization.service.interfaces.FileProcessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON file processor implementation.
 * Follows SOLID principles:
 * - Single Responsibility: Handles only JSON file operations
 * - Dependency Inversion: Depends on ObjectMapper abstraction
 * - Open/Closed: Can be extended without modifying existing code
 */
@Component
public class JsonFileProcessor implements FileProcessor {
    
    private final ObjectMapper objectMapper;
    
    public JsonFileProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public List<Map<String, Object>> parseFile(InputStream inputStream) throws IOException {
        // Read the entire input stream into a byte array first
        byte[] data = inputStream.readAllBytes();
        
        try {
            // Try to parse as array first
            return objectMapper.readValue(data, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            try {
                // If fails, try to parse as single object and wrap in mutable list
                Map<String, Object> singleObject = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});
                List<Map<String, Object>> result = new ArrayList<>();
                result.add(singleObject);
                return result;
            } catch (Exception ex) {
                throw new IOException("Failed to parse JSON file: " + ex.getMessage(), ex);
            }
        }
    }
    
    @Override
    public void writeFile(List<Map<String, Object>> data, Path outputPath) throws IOException {
        // If we have only one record, write it as a single object (not an array)
        // to maintain the original format for single JSON objects
        if (data.size() == 1) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), data.get(0));
        } else {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), data);
        }
    }
    
    @Override
    public String getFileExtension() {
        return "json";
    }
}
