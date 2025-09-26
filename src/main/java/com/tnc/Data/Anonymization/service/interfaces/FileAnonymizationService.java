package com.tnc.Data.Anonymization.service.interfaces;

import com.tnc.Data.Anonymization.dto.FileAnonymizationResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service interface for file-based anonymization operations.
 * Follows SOLID principles - Interface Segregation principle.
 */
public interface FileAnonymizationService {
    
    /**
     * Anonymize a file and save the result with _anon suffix
     * 
     * @param file The file to anonymize
     * @param strategy Anonymization strategy
     * @param preserveFormat Whether to preserve original format
     * @param seed Seed for consistent anonymization
     * @param customOutputFileName Optional custom output filename
     * @return FileAnonymizationResponse with processing results
     * @throws IOException if file processing fails
     */
    FileAnonymizationResponse anonymizeFile(MultipartFile file, String strategy, 
                                          boolean preserveFormat, Long seed, 
                                          String customOutputFileName) throws IOException;
    
    /**
     * Get the path to an anonymized file
     * 
     * @param fileName The anonymized file name
     * @return Path to the file
     */
    Path getAnonymizedFilePath(String fileName);
    
    /**
     * Check if a file exists in the anonymized files directory
     * 
     * @param fileName The file name to check
     * @return true if file exists, false otherwise
     */
    boolean anonymizedFileExists(String fileName);
    
    /**
     * Get download URL for an anonymized file
     * 
     * @param fileName The anonymized file name
     * @return Download URL
     */
    String getDownloadUrl(String fileName);
}
