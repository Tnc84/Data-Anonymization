package com.tnc.Data.Anonymization.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for common file operations.
 * Follows SOLID principles - Single Responsibility for file utilities only.
 * File format-specific logic has been moved to dedicated processors.
 */
@Component
public class FileUtil {
    
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
    
}
