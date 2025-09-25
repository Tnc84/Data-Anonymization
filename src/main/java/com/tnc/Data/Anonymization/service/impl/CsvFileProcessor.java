package com.tnc.Data.Anonymization.service.impl;

import com.tnc.Data.Anonymization.service.interfaces.FileProcessor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * CSV file processor implementation.
 * Follows SOLID principles:
 * - Single Responsibility: Handles only CSV file operations
 * - Open/Closed: Can be extended without modifying existing code
 */
@Component
public class CsvFileProcessor implements FileProcessor {
    
    @Override
    public List<Map<String, Object>> parseFile(InputStream inputStream) throws IOException {
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
    
    @Override
    public void writeFile(List<Map<String, Object>> data, Path outputPath) throws IOException {
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
    
    @Override
    public String getFileExtension() {
        return "csv";
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
