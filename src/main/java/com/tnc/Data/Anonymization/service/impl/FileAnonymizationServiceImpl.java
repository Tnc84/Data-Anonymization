package com.tnc.Data.Anonymization.service.impl;

import com.tnc.Data.Anonymization.dto.AnonymizationRequest;
import com.tnc.Data.Anonymization.dto.AnonymizationResponse;
import com.tnc.Data.Anonymization.dto.FileAnonymizationResponse;
import com.tnc.Data.Anonymization.service.interfaces.AnonymizationService;
import com.tnc.Data.Anonymization.service.interfaces.FileAnonymizationService;
import com.tnc.Data.Anonymization.service.interfaces.FileProcessor;
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
    private final FileProcessorFactory fileProcessorFactory;
    
    @Value("${anonymization.files.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${anonymization.files.output-dir:anonymized-files}")
    private String outputDir;
    
    @Value("${anonymization.files.anonymized-suffix:_anon}")
    private String anonymizedSuffix;
    
    
    public FileAnonymizationServiceImpl(AnonymizationService anonymizationService, 
                                      FileUtil fileUtil,
                                      FileProcessorFactory fileProcessorFactory) {
        this.anonymizationService = anonymizationService;
        this.fileUtil = fileUtil;
        this.fileProcessorFactory = fileProcessorFactory;
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
        if (!fileProcessorFactory.isSupported(fileExtension)) {
            return new FileAnonymizationResponse(false, 
                "File extension '" + fileExtension + "' is not supported. Supported extensions: " + 
                String.join(", ", fileProcessorFactory.getSupportedExtensions()));
        }
        
        try {
            logger.info("Processing file: {} with strategy: {}", originalFileName, strategy);
            
            // Ensure directories exist
            fileUtil.ensureDirectoryExists(uploadDir);
            fileUtil.ensureDirectoryExists(outputDir);
            
            // Get the appropriate processor for this file type
            FileProcessor processor = fileProcessorFactory.getProcessor(fileExtension);
            logger.debug("Using processor: {} for file: {}", processor.getClass().getSimpleName(), originalFileName);
            
            // Parse file using the processor
            List<Map<String, Object>> records;
            try (var inputStream = file.getInputStream()) {
                records = processor.parseFile(inputStream);
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
            
            // Write anonymized data to file using the processor
            processor.writeFile(records, outputPath);
            
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
    
}
