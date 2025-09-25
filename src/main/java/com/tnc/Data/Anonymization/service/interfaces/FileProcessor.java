package com.tnc.Data.Anonymization.service.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Interface for file processing operations.
 * Follows SOLID principles:
 * - Interface Segregation: Focused interface for file processing
 * - Dependency Inversion: Allows different implementations for different file types
 * - Open/Closed: Open for extension with new file types
 */
public interface FileProcessor {
    
    /**
     * Parse file content from input stream into a list of maps
     * @param inputStream the input stream containing file data
     * @return list of maps representing the parsed data
     * @throws IOException if parsing fails
     */
    List<Map<String, Object>> parseFile(InputStream inputStream) throws IOException;
    
    /**
     * Write data to file at the specified path
     * @param data the data to write
     * @param outputPath the path where to write the file
     * @throws IOException if writing fails
     */
    void writeFile(List<Map<String, Object>> data, Path outputPath) throws IOException;
    
    /**
     * Get the file extension this processor handles
     * @return the file extension (e.g., "csv", "json")
     */
    String getFileExtension();
    
    /**
     * Check if this processor supports the given file extension
     * @param extension the file extension to check
     * @return true if supported, false otherwise
     */
    default boolean supports(String extension) {
        return getFileExtension().equalsIgnoreCase(extension);
    }
}
