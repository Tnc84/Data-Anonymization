package com.tnc.Data.Anonymization.service.impl;

import com.tnc.Data.Anonymization.model.AnonymizationRequest;
import com.tnc.Data.Anonymization.model.AnonymizationResponse;
import com.tnc.Data.Anonymization.model.FileAnonymizationResponse;
import com.tnc.Data.Anonymization.service.interfaces.AnonymizationService;
import com.tnc.Data.Anonymization.service.interfaces.FileAnonymizationService;
import com.tnc.Data.Anonymization.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Implementation of FileAnonymizationService for processing files.
 * Follows SOLID principles:
 * - Single Responsibility: Handles file-based anonymization
 * - Dependency Inversion: Depends on AnonymizationService abstraction
 * - Open/Closed: Open for extension with new file formats
 */
@Service
public class FileAnonymizationServiceImpl implements FileAnonymizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileAnonymizationServiceImpl.class);
    
    private final AnonymizationService anonymizationService;
    private final FileUtil fileUtil;
    
    @Value("${anonymization.files.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${anonymization.files.output-dir:anonymized-files}")
    private String outputDir;
    
    @Value("${anonymization.files.anonymized-suffix:_anon}")
    private String anonymizedSuffix;
    
    @Value("${anonymization.files.allowed-extensions:csv,json}")
    private String[] allowedExtensions;
    
    public FileAnonymizationServiceImpl(AnonymizationService anonymizationService, FileUtil fileUtil) {
        this.anonymizationService = anonymizationService;
        this.fileUtil = fileUtil;
    }
    
    @Override
    public FileAnonymizationResponse anonymizeFile(MultipartFile file, String strategy, 
                                                 boolean preserveFormat, Long seed, 
                                                 String customOutputFileName) throws IOException {
        
        logger.info("Starting file anonymization - file: {}, strategy: {}, preserveFormat: {}, seed: {}", 
                   file != null ? file.getOriginalFilename() : "null", strategy, preserveFormat, seed);
        
        // Validate file
        if (file == null) {
            logger.error("File parameter is null");
            return new FileAnonymizationResponse(false, "File parameter is null");
        }
        
        if (file.isEmpty()) {
            logger.error("File is empty");
            return new FileAnonymizationResponse(false, "File is empty");
        }
        
        String originalFileName = file.getOriginalFilename();
        logger.debug("Original filename: {}", originalFileName);
        
        if (originalFileName == null) {
            logger.error("File name is null");
            return new FileAnonymizationResponse(false, "File name is null");
        }
        
        String fileExtension = fileUtil.getFileExtension(originalFileName);
        if (!isAllowedExtension(fileExtension)) {
            return new FileAnonymizationResponse(false, 
                "File extension '" + fileExtension + "' is not allowed. Allowed extensions: " + 
                String.join(", ", allowedExtensions));
        }
        
        try {
            logger.info("Processing file: {} with strategy: {}", originalFileName, strategy);
            
            // Ensure directories exist
            fileUtil.ensureDirectoryExists(uploadDir);
            fileUtil.ensureDirectoryExists(outputDir);
            
            // Parse file based on extension
            List<Map<String, Object>> records;
            try (var inputStream = file.getInputStream()) {
                if ("csv".equalsIgnoreCase(fileExtension)) {
                    logger.debug("Parsing CSV file: {}", originalFileName);
                    records = fileUtil.parseCsvFile(inputStream);
                } else if ("json".equalsIgnoreCase(fileExtension)) {
                    logger.debug("Parsing JSON file: {}", originalFileName);
                    records = fileUtil.parseJsonFile(inputStream);
                } else {
                    logger.error("Unsupported file format: {}", fileExtension);
                    return new FileAnonymizationResponse(false, "Unsupported file format: " + fileExtension);
                }
            } catch (Exception parseException) {
                logger.error("Failed to parse file: {} - {}", originalFileName, parseException.getMessage(), parseException);
                return new FileAnonymizationResponse(false, "Failed to parse file: " + parseException.getMessage());
            }
            
            if (records.isEmpty()) {
                return new FileAnonymizationResponse(false, "No data found in file");
            }
            
            // Anonymize each record
            int totalFieldsProcessed = 0;
            for (int i = 0; i < records.size(); i++) {
                Map<String, Object> record = records.get(i);
                
                AnonymizationRequest request = new AnonymizationRequest(record, strategy);
                request.setPreserveFormat(preserveFormat);
                request.setSeed(seed);
                
                AnonymizationResponse response = anonymizationService.anonymizeData(request);
                
                if (response.isSuccess()) {
                    records.set(i, response.getAnonymizedData());
                    totalFieldsProcessed += response.getFieldsProcessed();
                } else {
                    return new FileAnonymizationResponse(false, 
                        "Failed to anonymize record " + (i + 1) + ": " + response.getMessage());
                }
            }
            
            // Generate output filename
            String outputFileName = customOutputFileName != null ? 
                customOutputFileName : 
                fileUtil.generateAnonymizedFileName(originalFileName, anonymizedSuffix);
            
            Path outputPath = Paths.get(outputDir, outputFileName);
            
            // Write anonymized data to file
            if ("csv".equalsIgnoreCase(fileExtension)) {
                fileUtil.writeCsvFile(records, outputPath);
            } else if ("json".equalsIgnoreCase(fileExtension)) {
                fileUtil.writeJsonFile(records, outputPath);
            }
            
            // Create successful response
            FileAnonymizationResponse response = new FileAnonymizationResponse(
                originalFileName, outputFileName, strategy, records.size(), totalFieldsProcessed);
            
            response.setFilePath(outputPath.toString());
            response.setFileSize(Files.size(outputPath));
            response.setDownloadUrl(getDownloadUrl(outputFileName));
            
            return response;
            
        } catch (Exception e) {
            logger.error("Failed to process file: {} - {}", originalFileName, e.getMessage(), e);
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return new FileAnonymizationResponse(false, 
                "Failed to process file: " + errorMessage);
        }
    }
    
    @Override
    public Path getAnonymizedFilePath(String fileName) {
        return Paths.get(outputDir, fileName);
    }
    
    @Override
    public boolean anonymizedFileExists(String fileName) {
        return Files.exists(getAnonymizedFilePath(fileName));
    }
    
    @Override
    public String getDownloadUrl(String fileName) {
        return "/api/v1/anonymization/download/" + fileName;
    }
    
    private boolean isAllowedExtension(String extension) {
        for (String allowedExt : allowedExtensions) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
