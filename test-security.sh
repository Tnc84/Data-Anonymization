#!/bin/bash

echo "==============================================="
echo "Data Anonymization API - Security Test Script"
echo "==============================================="
echo

BASE_URL="http://localhost:8080/api/v1"

echo "1. Testing User Registration..."
curl -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser2","email":"testuser2@example.com","password":"password123","firstName":"Test","lastName":"User2"}' \
  -w "\n\nStatus: %{http_code}\n\n"

echo
echo "2. Testing User Login..."
ACCESS_TOKEN=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"bogdan","password":"password123"}' | jq -r .accessToken)

if [ "$ACCESS_TOKEN" == "null" ]; then
    echo "Login failed! Using default user..."
    ACCESS_TOKEN=$(curl -s -X POST $BASE_URL/auth/login \
      -H "Content-Type: application/json" \
      -d '{"usernameOrEmail":"admin","password":"password123"}' | jq -r .accessToken)
fi

echo "Access Token: $ACCESS_TOKEN"
echo

echo "3. Testing Authentication Status..."
curl -X GET $BASE_URL/auth/status \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -w "\n\nStatus: %{http_code}\n\n"

echo
echo "4. Testing User Profile..."
curl -X GET $BASE_URL/auth/profile \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -w "\n\nStatus: %{http_code}\n\n"

echo
echo "5. Testing Protected Anonymization Endpoint..."
curl -X POST $BASE_URL/anonymization/anonymize \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"data":{"name":"John Doe","email":"john@example.com"},"strategy":"MASKING"}' \
  -w "\n\nStatus: %{http_code}\n\n"

echo
echo "6. Testing Unauthorized Access (without token)..."
curl -X POST $BASE_URL/anonymization/anonymize \
  -H "Content-Type: application/json" \
  -d '{"data":{"name":"John Doe","email":"john@example.com"},"strategy":"MASKING"}' \
  -w "\n\nStatus: %{http_code}\n\n"

echo
echo "7. Testing Available Strategies (Protected)..."
curl -X GET $BASE_URL/anonymization/strategies \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -w "\n\nStatus: %{http_code}\n\n"

echo
echo "8. Testing File Upload (Protected)..."
echo '{"name":"John Doe","email":"john@example.com","phone":"123-456-7890"}' > temp_test.json
curl -X POST $BASE_URL/anonymization/upload-anonymize \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -F "file=@temp_test.json" \
  -F "strategy=MASKING" \
  -w "\n\nStatus: %{http_code}\n\n"
rm -f temp_test.json

echo
echo "==============================================="
echo "Security tests completed!"
echo "==============================================="
echo
echo "Default users for testing:"
echo "- Username: admin, Password: password123, Role: ADMIN"
echo "- Username: bogdan, Password: password123, Role: USER"
echo "- Username: lori, Password: password123, Role: USER"
echo "- Username: testuser, Password: password123, Role: USER"
echo
echo "Access Swagger UI at: http://localhost:8080/swagger-ui.html"
echo "Use \"Bearer $ACCESS_TOKEN\" for authorization"
echo "==============================================="
