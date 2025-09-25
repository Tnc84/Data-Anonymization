package com.tnc.Data.Anonymization.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Utility class for file operations including CSV and JSON parsing.
 * Follows SOLID principles - Single Responsibility for file operations.
 */
@Component
public class FileUtil {
    
    private final ObjectMapper objectMapper;
    
    public FileUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Parse CSV file and convert to List of Maps
     */
    public List<Map<String, Object>> parseCsvFile(InputStream inputStream) throws IOException {
        List<Map<String, Object>> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return records;
            }
            
            String[] headers = parseCSVLine(headerLine);
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] values = parseCSVLine(line);
                Map<String, Object> record = new HashMap<>();
                
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    record.put(headers[i].trim(), values[i].trim());
                }
                
                records.add(record);
            }
        }
        
        return records;
    }
    
    /**
     * Parse JSON file and convert to List of Maps
     */
    public List<Map<String, Object>> parseJsonFile(InputStream inputStream) throws IOException {
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
    
    /**
     * Write data to CSV file
     */
    public void writeCsvFile(List<Map<String, Object>> data, Path outputPath) throws IOException {
        if (data.isEmpty()) {
            Files.createFile(outputPath);
            return;
        }
        
        // Get all unique keys from all records to create headers
        Set<String> allKeys = new LinkedHashSet<>();
        for (Map<String, Object> record : data) {
            allKeys.addAll(record.keySet());
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            // Write headers
            writer.write(String.join(",", allKeys));
            writer.newLine();
            
            // Write data rows
            for (Map<String, Object> record : data) {
                List<String> values = new ArrayList<>();
                for (String key : allKeys) {
                    Object value = record.get(key);
                    String csvValue = value != null ? escapeCsvValue(value.toString()) : "";
                    values.add(csvValue);
                }
                writer.write(String.join(",", values));
                writer.newLine();
            }
        }
    }
    
    /**
     * Write data to JSON file
     */
    public void writeJsonFile(List<Map<String, Object>> data, Path outputPath) throws IOException {
        // If we have only one record, write it as a single object (not an array)
        // to maintain the original format for single JSON objects
        if (data.size() == 1) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), data.get(0));
        } else {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputPath.toFile(), data);
        }
    }
    
    /**
     * Generate anonymized filename with suffix
     */
    public String generateAnonymizedFileName(String originalFileName, String suffix) {
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return originalFileName + suffix;
        }
        
        String nameWithoutExtension = originalFileName.substring(0, lastDotIndex);
        String extension = originalFileName.substring(lastDotIndex);
        return nameWithoutExtension + suffix + extension;
    }
    
    /**
     * Get file extension
     */
    public String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * Create directory if it doesn't exist
     */
    public void ensureDirectoryExists(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
    
    /**
     * Parse a single CSV line handling quoted values and commas within quotes
     */
    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        
        result.add(currentField.toString());
        return result.toArray(new String[0]);
    }
    
    /**
     * Escape CSV values that contain commas, quotes, or newlines
     */
    private String escapeCsvValue(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
