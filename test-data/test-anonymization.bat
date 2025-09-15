@echo off
REM Test script for Data Anonymization API (Windows)
set BASE_URL=http://localhost:8080/api/v1/anonymization

echo === Data Anonymization API Test Script ===
echo.

REM Test 1: Health Check
echo 1. Testing Health Check...
curl -s "%BASE_URL%/health"
echo.
echo.

REM Test 2: Get Available Strategies
echo 2. Getting Available Strategies...
curl -s "%BASE_URL%/strategies"
echo.
echo.

REM Test 3: Quick Anonymization
echo 3. Testing Quick Anonymization...
curl -s -X POST "%BASE_URL%/quick-anonymize" ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"phone\":\"555-123-4567\",\"ssn\":\"123-45-6789\"}"
echo.
echo.

REM Test 4: Pseudonymization Strategy
echo 4. Testing Pseudonymization Strategy...
curl -s -X POST "%BASE_URL%/anonymize" ^
  -H "Content-Type: application/json" ^
  -d "{\"data\":{\"name\":\"Jane Smith\",\"email\":\"jane.smith@company.com\",\"phone\":\"(555) 987-6543\",\"ssn\":\"987-65-4321\"},\"strategy\":\"PSEUDONYMIZATION\",\"preserveFormat\":true,\"seed\":12345}"
echo.
echo.

REM Test 5: Masking Strategy
echo 5. Testing Masking Strategy...
curl -s -X POST "%BASE_URL%/anonymize" ^
  -H "Content-Type: application/json" ^
  -d "{\"data\":{\"firstName\":\"Michael\",\"lastName\":\"Johnson\",\"email\":\"m.johnson@tech.org\",\"creditCard\":\"4111111111111111\",\"salary\":95000},\"strategy\":\"MASKING\",\"preserveFormat\":true}"
echo.
echo.

REM Test 6: Redaction Strategy  
echo 6. Testing Redaction Strategy...
curl -s -X POST "%BASE_URL%/anonymize" ^
  -H "Content-Type: application/json" ^
  -d "{\"data\":{\"name\":\"Alice Williams\",\"ssn\":\"111-22-3333\",\"creditCard\":\"6011-0009-9013-9424\",\"address\":\"321 Elm Street, Boston, MA 02101\"},\"strategy\":\"REDACTION\",\"preserveFormat\":false}"
echo.
echo.

echo === Test Complete ===
pause
