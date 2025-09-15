package com.tnc.Data.Anonymization.service.impl;

import com.github.javafaker.Faker;
import com.tnc.Data.Anonymization.enums.DataType;
import com.tnc.Data.Anonymization.service.interfaces.DataAnonymizer;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

/**
 * Implementation of DataAnonymizer using JavaFaker library.
 * Follows SOLID principles:
 * - Single Responsibility: Handles fake data generation
 * - Open/Closed: Can be extended for new data types
 * - Liskov Substitution: Can replace any DataAnonymizer implementation
 */
@Component("fakeDataAnonymizer")
public class FakeDataAnonymizer implements DataAnonymizer {
    
    private final Faker defaultFaker;
    
    public FakeDataAnonymizer() {
        this.defaultFaker = new Faker();
    }
    
    @Override
    public Object anonymize(Object value, DataType dataType, boolean preserveFormat, Long seed) {
        if (value == null) {
            return null;
        }
        
        Faker faker = seed != null ? new Faker(new Random(seed)) : defaultFaker;
        
        return switch (dataType) {
            case NAME -> generateName(value, faker, preserveFormat);
            case EMAIL -> generateEmail(value, faker, preserveFormat);
            case PHONE -> generatePhone(value, faker, preserveFormat);
            case ADDRESS -> generateAddress(value, faker, preserveFormat);
            case SSN -> generateSSN(value, faker, preserveFormat);
            case CREDIT_CARD -> generateCreditCard(value, faker, preserveFormat);
            case DATE -> generateDate(value, faker, preserveFormat);
            case NUMBER -> generateNumber(value, faker, preserveFormat);
            case ID -> generateId(value, faker, preserveFormat);
            case BOOLEAN -> faker.bool().bool();
            case TEXT -> generateText(value, faker, preserveFormat);
            case UNKNOWN -> generateGenericFakeData(value, faker);
        };
    }
    
    @Override
    public Object anonymize(Object value, String fieldName, boolean preserveFormat, Long seed) {
        DataType dataType = DataType.classifyFromFieldName(fieldName);
        return anonymize(value, dataType, preserveFormat, seed);
    }
    
    @Override
    public boolean supports(DataType dataType) {
        return true; // This implementation supports all data types
    }
    
    private Object generateName(Object value, Faker faker, boolean preserveFormat) {
        String original = value.toString();
        
        if (original.contains(" ")) {
            // Full name
            return faker.name().fullName();
        } else if (original.length() > 0 && Character.isUpperCase(original.charAt(0))) {
            // Likely first name
            return faker.name().firstName();
        } else {
            return faker.name().lastName();
        }
    }
    
    private Object generateEmail(Object value, Faker faker, boolean preserveFormat) {
        if (preserveFormat) {
            String original = value.toString();
            String domain = original.contains("@") ? original.substring(original.indexOf("@")) : "@example.com";
            return faker.name().username() + domain;
        }
        return faker.internet().emailAddress();
    }
    
    private Object generatePhone(Object value, Faker faker, boolean preserveFormat) {
        if (preserveFormat) {
            String original = value.toString();
            String pattern = original.replaceAll("\\d", "#");
            return faker.numerify(pattern);
        }
        return faker.phoneNumber().phoneNumber();
    }
    
    private Object generateAddress(Object value, Faker faker, boolean preserveFormat) {
        String original = value.toString().toLowerCase();
        
        if (original.contains("street") || original.contains("avenue") || original.contains("road")) {
            return faker.address().streetAddress();
        } else if (original.contains("city")) {
            return faker.address().city();
        } else if (original.contains("zip") || original.contains("postal")) {
            return faker.address().zipCode();
        } else if (original.contains("state") || original.contains("province")) {
            return faker.address().state();
        } else if (original.contains("country")) {
            return faker.address().country();
        }
        
        return faker.address().fullAddress();
    }
    
    private Object generateSSN(Object value, Faker faker, boolean preserveFormat) {
        if (preserveFormat) {
            return faker.numerify("###-##-####");
        }
        return faker.idNumber().ssnValid();
    }
    
    private Object generateCreditCard(Object value, Faker faker, boolean preserveFormat) {
        if (preserveFormat) {
            String original = value.toString();
            if (original.length() == 16) {
                return faker.numerify("################");
            } else if (original.contains("-")) {
                return faker.numerify("####-####-####-####");
            }
        }
        return faker.finance().creditCard();
    }
    
    private Object generateDate(Object value, Faker faker, boolean preserveFormat) {
        Date fakeDate = faker.date().birthday(18, 80);
        
        if (value instanceof LocalDate) {
            return fakeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (value instanceof Date) {
            return fakeDate;
        } else {
            // String date - try to preserve format
            return fakeDate.toString();
        }
    }
    
    private Object generateNumber(Object value, Faker faker, boolean preserveFormat) {
        if (value instanceof Integer) {
            return faker.number().numberBetween(1, 100000);
        } else if (value instanceof Long) {
            return faker.number().numberBetween(1L, 1000000L);
        } else if (value instanceof Double) {
            return faker.number().randomDouble(2, 1, 10000);
        } else if (value instanceof Float) {
            return (float) faker.number().randomDouble(2, 1, 10000);
        }
        
        return faker.number().numberBetween(1, 100000);
    }
    
    private Object generateId(Object value, Faker faker, boolean preserveFormat) {
        if (preserveFormat) {
            String original = value.toString();
            if (StringUtils.isNumeric(original)) {
                return faker.number().numberBetween(100000, 999999);
            } else if (original.matches("[A-Za-z]+\\d+")) {
                return faker.regexify("[A-Z]{2}\\d{6}");
            }
        }
        
        return faker.idNumber().valid();
    }
    
    private Object generateText(Object value, Faker faker, boolean preserveFormat) {
        String original = value.toString();
        int wordCount = original.split("\\s+").length;
        
        if (wordCount == 1) {
            return faker.lorem().word();
        } else if (wordCount <= 5) {
            return faker.lorem().words(wordCount);
        } else {
            return faker.lorem().sentence(Math.min(wordCount, 20));
        }
    }
    
    private Object generateGenericFakeData(Object value, Faker faker) {
        if (value instanceof String) {
            return faker.lorem().word();
        } else if (value instanceof Number) {
            return faker.number().numberBetween(1, 1000);
        } else if (value instanceof Boolean) {
            return faker.bool().bool();
        }
        
        return faker.lorem().word();
    }
}
