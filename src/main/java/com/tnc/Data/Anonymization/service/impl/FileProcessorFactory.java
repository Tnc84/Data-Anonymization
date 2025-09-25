package com.tnc.Data.Anonymization.service.impl;

import com.tnc.Data.Anonymization.service.interfaces.FileProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Factory for creating file processors based on file extension.
 * Follows SOLID principles:
 * - Single Responsibility: Creates and manages file processors
 * - Open/Closed: Open for extension with new file types
 * - Dependency Inversion: Depends on FileProcessor abstraction
 */
@Component
public class FileProcessorFactory {
    
    private final Map<String, FileProcessor> processors;
    
    /**
     * Constructor that automatically registers all FileProcessor implementations
     * @param processorList List of all FileProcessor beans from Spring context
     */
    public FileProcessorFactory(List<FileProcessor> processorList) {
        this.processors = processorList.stream()
            .collect(Collectors.toMap(
                processor -> processor.getFileExtension().toLowerCase(), 
                processor -> processor
            ));
    }
    
    /**
     * Get the appropriate processor for the given file extension
     * @param fileExtension the file extension (e.g., "csv", "json")
     * @return the processor that handles this file type
     * @throws UnsupportedOperationException if no processor is found for the extension
     */
    public FileProcessor getProcessor(String fileExtension) {
        if (fileExtension == null || fileExtension.trim().isEmpty()) {
            throw new IllegalArgumentException("File extension cannot be null or empty");
        }
        
        FileProcessor processor = processors.get(fileExtension.toLowerCase().trim());
        if (processor == null) {
            throw new UnsupportedOperationException(
                "Unsupported file type: " + fileExtension + 
                ". Supported types: " + String.join(", ", processors.keySet())
            );
        }
        return processor;
    }
    
    /**
     * Check if a file extension is supported
     * @param fileExtension the file extension to check
     * @return true if supported, false otherwise
     */
    public boolean isSupported(String fileExtension) {
        if (fileExtension == null || fileExtension.trim().isEmpty()) {
            return false;
        }
        return processors.containsKey(fileExtension.toLowerCase().trim());
    }
    
    /**
     * Get all supported file extensions
     * @return set of supported file extensions
     */
    public java.util.Set<String> getSupportedExtensions() {
        return processors.keySet();
    }
}
