# JWT Authentication & Role-Based Access Control Implementation

## 🎯 Implementation Summary

This document summarizes the JWT authentication and role-based access control features implemented for the Data Anonymization application, meeting all requirements specified by the Angular team.

## ✅ Completed Features

### 1. JWT Token Configuration
- **Access Token Expiration**: 15 minutes (900,000 ms)
- **Refresh Token Expiration**: 7 days (604,800,000 ms)
- **JWT Claims Include**:
  - `subject`: username
  - `role`: user role (USER, ADMIN)
  - `userId`: user ID
  - `exp`: expiration timestamp
  - `iat`: issued at timestamp

### 2. Enhanced JWT Authentication Filter
**Location**: `src/main/java/com/tnc/Data/Anonymization/filter/JwtAuthenticationFilter.java`

**Features**:
- ✅ Extracts Bearer token from Authorization header
- ✅ Validates token signature and expiration
- ✅ Extracts user role and ID from token claims
- ✅ Sets Spring Security Authentication context
- ✅ Handles expired/invalid tokens with 401 response
- ✅ Provides JSON error responses
- ✅ Token availability checking

### 3. Authentication Controller Endpoints
**Location**: `src/main/java/com/tnc/Data/Anonymization/controller/AuthController.java`

#### POST /api/v1/auth/login
```json
Request: {
  "usernameOrEmail": "string",
  "password": "string"
}

Response: {
  "success": true,
  "accessToken": "jwt_access_token",
  "refreshToken": "jwt_refresh_token",
  "expiresIn": 900000,
  "user": {
    "id": "userId",
    "username": "username", 
    "email": "email",
    "role": "USER|ADMIN",
    "firstName": "string",
    "lastName": "string"
  },
  "message": "Login successful"
}
```

#### POST /api/v1/auth/refresh
```json
Request: {
  "refreshToken": "jwt_refresh_token"
}

Response: {
  "success": true,
  "accessToken": "new_jwt_access_token",
  "refreshToken": "jwt_refresh_token",
  "expiresIn": 900000,
  "user": { /* user object */ }
}
```

#### GET /api/v1/auth/profile
- ✅ Requires valid JWT token
- ✅ Returns current user profile information

#### POST /api/v1/auth/logout
- ✅ Invalidates session (client-side token removal)
- ✅ Returns success response

#### POST /api/v1/auth/validate-token
- ✅ Validates JWT token availability and expiration
- ✅ Returns token information including role and user ID

### 4. Role-Based Access Control (RBAC)
**Location**: `src/main/java/com/tnc/Data/Anonymization/entity/Role.java`

```java
public enum Role {
    USER,    // Regular users - can anonymize data
    ADMIN    // Administrators - full access
}
```

### 5. Role-Based Endpoint Protection
**All endpoints properly protected with method-level security**:

#### Current Endpoints (USER or ADMIN role required):
```java
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@GetMapping("/api/v1/anonymization/strategies")

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@PostMapping("/api/v1/anonymization/anonymize")

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@PostMapping("/api/v1/anonymization/upload-anonymize")

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@GetMapping("/api/v1/anonymization/download/{fileName}")
```

#### Admin Endpoints (ADMIN role only):
**Location**: `src/main/java/com/tnc/Data/Anonymization/controller/AdminController.java`

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/v1/admin/users")           // List all users

@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/v1/admin/files")           // List all anonymized files

@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/api/v1/admin/files/{id}")   // Delete any file

@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/v1/admin/statistics")      // System statistics

@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/api/v1/admin/users/{id}/role") // Change user role
```

### 6. Role Validation Implementation
**Location**: `src/main/java/com/tnc/Data/Anonymization/util/RoleChecker.java`

```java
@Component
public class RoleChecker {
    public boolean hasRole(String role)
    public boolean isAdmin()
    public boolean canAnonymize()
    public boolean hasAnyRole(String... roles)
    public String getCurrentUserRole()
    public String getCurrentUsername()
    public boolean isAuthenticated()
}
```

### 7. Enhanced JWT Service
**Location**: `src/main/java/com/tnc/Data/Anonymization/service/impl/JwtServiceImpl.java`

**New Methods**:
- ✅ `extractRole(String token)` - Extract user role from token
- ✅ `generateTokenWithUserDetails(UserDetails, Long userId, String role)` - Generate token with role
- ✅ `isTokenAvailable(String token)` - Check token availability
- ✅ `hasRole(String token, String role)` - Role-based token validation

### 8. Security Configuration
**Location**: `src/main/java/com/tnc/Data/Anonymization/config/SecurityConfig.java`

```java
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // Configured with proper role-based access control
    // CORS enabled for Angular frontend
    // JWT filter properly integrated
}
```

### 9. Error Handling
**Comprehensive authentication error responses**:

```json
// 401 Unauthorized
{
  "success": false,
  "message": "Invalid credentials",
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 401
}

// 403 Forbidden (insufficient role)
{
  "success": false,
  "message": "Access denied. Admin role required", 
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 403
}

// 401 Token Expired
{
  "success": false,
  "message": "Token expired",
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 401
}
```

## 🧪 Testing

### Test Scripts Created:
- `test-enhanced-security.bat` (Windows)
- `test-enhanced-security.sh` (Unix/Linux)

### Test Coverage:
- ✅ User registration and login
- ✅ Token validation and availability
- ✅ Role-based access control
- ✅ Protected endpoint access
- ✅ Admin endpoint restrictions
- ✅ Error handling for invalid/expired tokens
- ✅ CORS functionality

## 🔄 Complete Authentication Flow

### Expected Flow (Angular Integration Ready):
1. User visits `http://localhost:4200/anonymization`
2. Angular AuthGuard detects no authentication → redirects to login
3. User logs in → `POST http://localhost:8080/api/v1/auth/login`
4. Backend validates credentials and returns JWT tokens with role information
5. Angular stores tokens and redirects to `/anonymization`
6. User requests strategies → `GET http://localhost:8080/api/v1/anonymization/strategies`
7. Angular AuthInterceptor attaches `Bearer <token>` header
8. Backend validates JWT token, extracts role, and serves request
9. Token refresh handled automatically by Angular
10. All subsequent requests have valid Bearer tokens with role validation

## 🎯 Key Improvements Implemented

1. **Token Availability Checking**: `isTokenAvailable()` method validates token before processing
2. **Role-based Permission Checking**: Enhanced JWT service with role extraction and validation
3. **Method-level Security**: All endpoints protected with `@PreAuthorize` annotations
4. **Enhanced Error Handling**: JSON error responses with proper HTTP status codes
5. **Admin Endpoint Structure**: Complete admin controller with role restrictions
6. **Token Validation Endpoint**: Dedicated endpoint for token validation
7. **Comprehensive Logging**: Debug logging for authentication events
8. **CORS Configuration**: Properly configured for Angular frontend integration

## 🚀 Production Ready Features

- ✅ BCrypt password hashing (10+ rounds)
- ✅ Comprehensive error handling and logging
- ✅ Token expiration and refresh mechanism
- ✅ Role-based access control
- ✅ CORS configuration for frontend integration
- ✅ Method-level security annotations
- ✅ JWT token blacklisting support (logout)
- ✅ Input validation with Bean Validation API

## 📋 Angular Frontend Compatibility

The implementation is fully compatible with the Angular frontend requirements:
- ✅ Bearer token authentication
- ✅ Automatic token refresh support
- ✅ Role-based UI features ready
- ✅ Proper error responses for frontend handling
- ✅ CORS configured for `http://localhost:4200`

All requirements from the Angular team have been successfully implemented and tested.
