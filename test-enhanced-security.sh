#!/bin/bash

echo "Testing Enhanced JWT Authentication and Role-Based Access Control"
echo "================================================================"

BASE_URL="http://localhost:8080/api/v1"

echo
echo "1. Testing user registration..."
curl -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"testuser@example.com","password":"password123","firstName":"Test","lastName":"User"}' \
  -w "Status: %{http_code}\n" -s

echo
echo "2. Testing user login..."
RESPONSE=$(curl -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"testuser","password":"password123"}' \
  -w "Status: %{http_code}\n" -s)

echo "$RESPONSE"

echo
echo "3. Extracting access token..."
TOKEN=$(echo "$RESPONSE" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
echo "Token extracted (first 50 chars): ${TOKEN:0:50}..."

echo
echo "4. Testing token validation..."
curl -X POST "$BASE_URL/auth/validate-token" \
  -H "Authorization: Bearer $TOKEN" \
  -w "Status: %{http_code}\n" -s

echo
echo "5. Testing profile endpoint (requires authentication)..."
curl -X GET "$BASE_URL/auth/profile" \
  -H "Authorization: Bearer $TOKEN" \
  -w "Status: %{http_code}\n" -s

echo
echo "6. Testing anonymization strategies (requires USER or ADMIN role)..."
curl -X GET "$BASE_URL/anonymization/strategies" \
  -H "Authorization: Bearer $TOKEN" \
  -w "Status: %{http_code}\n" -s

echo
echo "7. Testing admin endpoint (should fail for USER role)..."
curl -X GET "$BASE_URL/admin/statistics" \
  -H "Authorization: Bearer $TOKEN" \
  -w "Status: %{http_code}\n" -s

echo
echo "8. Testing access without token (should fail)..."
curl -X GET "$BASE_URL/anonymization/strategies" \
  -w "Status: %{http_code}\n" -s

echo
echo "9. Testing access with invalid token (should fail)..."
curl -X GET "$BASE_URL/anonymization/strategies" \
  -H "Authorization: Bearer invalid-token" \
  -w "Status: %{http_code}\n" -s

echo
echo "10. Testing logout..."
curl -X POST "$BASE_URL/auth/logout" \
  -H "Authorization: Bearer $TOKEN" \
  -w "Status: %{http_code}\n" -s

echo
echo "Testing completed!"
echo "Expected results:"
echo "- Registration: 201 Created"
echo "- Login: 200 OK"
echo "- Token validation: 200 OK"
echo "- Profile: 200 OK"
echo "- Strategies: 200 OK"
echo "- Admin endpoint: 403 Forbidden (USER role cannot access)"
echo "- No token: 401 Unauthorized"
echo "- Invalid token: 401 Unauthorized"
echo "- Logout: 200 OK"
