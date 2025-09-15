package com.tnc.Data.Anonymization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnc.Data.Anonymization.model.AnonymizationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Data Anonymization application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnonymizationIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/anonymization/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Data Anonymization Service"));
    }
    
    @Test
    public void testGetAvailableStrategies() throws Exception {
        mockMvc.perform(get("/api/v1/anonymization/strategies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.strategies").isArray())
                .andExpect(jsonPath("$.count").isNumber());
    }
    
    @Test
    public void testQuickAnonymization() throws Exception {
        Map<String, Object> testData = new HashMap<>();
        testData.put("firstName", "John");
        testData.put("lastName", "Doe");
        testData.put("email", "john.doe@example.com");
        testData.put("phone", "555-123-4567");
        
        mockMvc.perform(post("/api/v1/anonymization/quick-anonymize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.firstName").exists())
                .andExpect(jsonPath("$.data.lastName").exists())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.phone").exists());
    }
    
    @Test
    public void testFullAnonymization() throws Exception {
        Map<String, Object> testData = new HashMap<>();
        testData.put("name", "John Doe");
        testData.put("email", "john.doe@example.com");
        testData.put("ssn", "123-45-6789");
        
        AnonymizationRequest request = new AnonymizationRequest(testData, "MASKING");
        request.setPreserveFormat(true);
        request.setSeed(12345L);
        
        mockMvc.perform(post("/api/v1/anonymization/anonymize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.anonymizedData").exists())
                .andExpect(jsonPath("$.strategy").value("MASKING"))
                .andExpect(jsonPath("$.fieldsProcessed").isNumber());
    }
    
    @Test
    public void testPseudonymization() throws Exception {
        Map<String, Object> testData = new HashMap<>();
        testData.put("userId", "user123");
        testData.put("email", "user@example.com");
        
        AnonymizationRequest request = new AnonymizationRequest(testData, "PSEUDONYMIZATION");
        request.setPreserveFormat(true);
        request.setSeed(54321L);
        
        mockMvc.perform(post("/api/v1/anonymization/anonymize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.strategy").value("PSEUDONYMIZATION"));
    }
    
    @Test
    public void testBatchAnonymization() throws Exception {
        Map<String, Object> batchRequest = new HashMap<>();
        
        Map<String, Map<String, Object>> datasets = new HashMap<>();
        
        Map<String, Object> dataset1 = new HashMap<>();
        dataset1.put("name", "Alice Smith");
        dataset1.put("email", "alice@example.com");
        datasets.put("users", dataset1);
        
        Map<String, Object> dataset2 = new HashMap<>();
        dataset2.put("firstName", "Bob");
        dataset2.put("lastName", "Johnson");
        datasets.put("employees", dataset2);
        
        batchRequest.put("datasets", datasets);
        batchRequest.put("strategy", "MASKING");
        batchRequest.put("preserveFormat", true);
        
        mockMvc.perform(post("/api/v1/anonymization/batch-anonymize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.datasets.users").exists())
                .andExpect(jsonPath("$.datasets.employees").exists())
                .andExpect(jsonPath("$.totalFieldsProcessed").isNumber());
    }
    
    @Test
    public void testInvalidStrategy() throws Exception {
        Map<String, Object> testData = new HashMap<>();
        testData.put("name", "John Doe");
        
        AnonymizationRequest request = new AnonymizationRequest(testData, "INVALID_STRATEGY");
        
        mockMvc.perform(post("/api/v1/anonymization/anonymize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    public void testValidationError() throws Exception {
        AnonymizationRequest request = new AnonymizationRequest();
        // Missing required fields
        
        mockMvc.perform(post("/api/v1/anonymization/anonymize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").exists());
    }
}
