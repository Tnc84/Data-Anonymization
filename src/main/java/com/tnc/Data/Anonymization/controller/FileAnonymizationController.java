package com.tnc.Data.Anonymization.controller;

import com.tnc.Data.Anonymization.dto.FileAnonymizationResponse;
import com.tnc.Data.Anonymization.service.interfaces.FileAnonymizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * REST Controller for file-based anonymization operations.
 * Follows SOLID principles:
 * - Single Responsibility: Handles HTTP requests for file anonymization
 * - Dependency Inversion: Depends on FileAnonymizationService abstraction
 */
@RestController
@RequestMapping("/api/v1/anonymization")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "File Anonymization", description = "APIs for anonymizing files (CSV, JSON) with automatic folder management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class FileAnonymizationController {
    
    private final FileAnonymizationService fileAnonymizationService;
    
    /**
     * Upload and anonymize a file
     */
    @Operation(
        summary = "Upload and anonymize a file",
        description = "Uploads a CSV or JSON file, anonymizes it using the specified strategy, and saves it with '_anon' suffix in the anonymized-files folder"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File anonymized successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileAnonymizationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid file or request parameters",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = "application/json"))
    })
    @PostMapping(value = "/upload-anonymize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FileAnonymizationResponse> uploadAndAnonymize(
            @Parameter(description = "File to anonymize (CSV or JSON)", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Anonymization strategy", example = "MASKING")
            @RequestParam(value = "strategy", defaultValue = "MASKING") String strategy,
            
            @Parameter(description = "Preserve original format", example = "true")
            @RequestParam(value = "preserveFormat", defaultValue = "true") boolean preserveFormat,
            
            @Parameter(description = "Seed for consistent anonymization", example = "12345")
            @RequestParam(value = "seed", required = false) Long seed,
            
            @Parameter(description = "Custom output filename (optional)")
            @RequestParam(value = "outputFileName", required = false) String outputFileName) {
        
        try {
            FileAnonymizationResponse response = fileAnonymizationService.anonymizeFile(
                file, strategy, preserveFormat, seed, outputFileName);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (IOException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            FileAnonymizationResponse errorResponse = new FileAnonymizationResponse(false, 
                "File processing failed: " + errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            FileAnonymizationResponse errorResponse = new FileAnonymizationResponse(false, 
                "Unexpected error: " + errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Download an anonymized file
     */
    @Operation(
        summary = "Download an anonymized file",
        description = "Downloads a previously anonymized file from the anonymized-files folder"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
        @ApiResponse(responseCode = "404", description = "File not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/download/{fileName}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Name of the anonymized file to download", required = true)
            @PathVariable String fileName) {
        
        try {
            if (!fileAnonymizationService.anonymizedFileExists(fileName)) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = fileAnonymizationService.getAnonymizedFilePath(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            // Determine content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * List all anonymized files
     */
    @Operation(
        summary = "List all anonymized files",
        description = "Lists all files in the anonymized-files folder"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Files listed successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/files")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> listAnonymizedFiles() {
        try {
            Path anonymizedDir = fileAnonymizationService.getAnonymizedFilePath("");
            
            if (!Files.exists(anonymizedDir)) {
                return ResponseEntity.ok(new String[0]);
            }
            
            String[] files = Files.list(anonymizedDir)
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .toArray(String[]::new);
            
            return ResponseEntity.ok(files);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to list files: " + e.getMessage());
        }
    }
}
