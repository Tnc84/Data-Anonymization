@echo off
echo Testing File Anonymization Feature
echo ===================================

echo.
echo Starting the application...
start /B mvn spring-boot:run

echo Waiting for application to start...
timeout /T 10 > nul

echo.
echo Testing file upload and anonymization...
echo.

REM Test file upload with anonymization
curl -X POST "http://localhost:8080/api/v1/anonymization/upload-anonymize" ^
     -H "Content-Type: multipart/form-data" ^
     -F "file=@test-data/test.csv" ^
     -F "strategy=MASKING" ^
     -F "preserveFormat=true"

echo.
echo.
echo Listing anonymized files...
curl -X GET "http://localhost:8080/api/v1/anonymization/files"

echo.
echo.
echo Test completed! Check the 'anonymized-files' folder for the anonymized file.
echo The file should be named 'test_anon.csv'

pause
