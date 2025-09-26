# Data Anonymization API - Security Implementation

## Overview

The Data Anonymization API now includes comprehensive JWT-based authentication and authorization to ensure that only authorized users can access anonymization services. This implementation follows enterprise security best practices and Spring Security standards.

## Security Features

### 1. JWT Authentication
- **Token-based authentication** using JSON Web Tokens (JWT)
- **Stateless security** - no server-side session storage
- **Configurable token expiration** (default: 24 hours for access tokens, 7 days for refresh tokens)
- **Secure token signing** using HMAC-SHA256 algorithm

### 2. User Management
- **User registration** with email validation
- **Role-based access control** (USER, ADMIN)
- **Password encryption** using BCrypt
- **User profile management**
- **Account status management** (enabled/disabled, locked, expired)

### 3. Database Security
- **Secure user storage** with encrypted passwords
- **Unique constraints** on username and email
- **Audit fields** (created_at, updated_at, last_login)
- **H2 database** with file-based persistence

## API Endpoints

### Authentication Endpoints (`/api/v1/auth`)

| Method | Endpoint | Description | Public |
|--------|----------|-------------|--------|
| POST | `/register` | Register a new user | ✅ |
| POST | `/login` | Authenticate user login | ✅ |
| POST | `/refresh` | Refresh JWT token | ✅ |
| GET | `/profile` | Get current user profile | 🔒 |
| POST | `/logout` | Logout user | 🔒 |
| GET | `/status` | Check authentication status | 🔒 |
| GET | `/stats` | Get user statistics | 🔒 |

### Protected Endpoints

All anonymization endpoints (`/api/v1/anonymization/**`) now require authentication:
- `/anonymize` - Anonymize data
- `/quick-anonymize` - Quick anonymization with defaults
- `/upload-anonymize` - Upload and anonymize files
- `/download/{fileName}` - Download anonymized files
- `/files` - List anonymized files
- `/strategies` - Get available strategies
- `/batch-anonymize` - Batch anonymization

## Usage Guide

### 1. User Registration

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securePassword123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### 2. User Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "johndoe",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Authentication successful",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER",
    "enabled": true
  }
}
```

### 3. Using Protected Endpoints

Include the JWT token in the Authorization header:

```bash
curl -X POST http://localhost:8080/api/v1/anonymization/anonymize \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "data": {"name": "John Doe", "email": "john@example.com"},
    "strategy": "MASKING"
  }'
```

### 4. Token Refresh

When your access token expires, use the refresh token:

```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

## Default Users

The system comes with pre-configured test users:

| Username | Email | Password | Role |
|----------|--------|----------|------|
| admin | admin@anonymization.com | password123 | ADMIN |
| bogdan | bogdan@anonymization.com | password123 | USER |
| lori | lori@anonymization.com | password123 | USER |
| testuser | test@anonymization.com | password123 | USER |

## Configuration

### JWT Configuration (application.properties)

```properties
# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000          # 24 hours in milliseconds
jwt.refresh-expiration=604800000 # 7 days in milliseconds
```

### Security Features

1. **CORS Configuration** - Allows cross-origin requests for frontend integration
2. **CSRF Protection** - Disabled for stateless JWT implementation
3. **Session Management** - Stateless (no server-side sessions)
4. **Password Encoding** - BCrypt with strength 12
5. **Method-level Security** - `@PreAuthorize` annotations for fine-grained control

## Frontend Integration

### 1. Login Flow
1. Send credentials to `/api/v1/auth/login`
2. Store the `accessToken` and `refreshToken` (localStorage/sessionStorage)
3. Include `Authorization: Bearer {accessToken}` in all API requests

### 2. Token Management
- Monitor token expiration using the `expiresIn` field
- Automatically refresh tokens before expiration
- Handle 401 responses by redirecting to login

### 3. Example JavaScript Integration

```javascript
// Login function
async function login(username, password) {
  const response = await fetch('/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ usernameOrEmail: username, password })
  });
  
  const data = await response.json();
  if (data.success) {
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    return data.user;
  }
  throw new Error(data.message);
}

// API call with authentication
async function anonymizeData(data) {
  const token = localStorage.getItem('accessToken');
  const response = await fetch('/api/v1/anonymization/anonymize', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(data)
  });
  
  if (response.status === 401) {
    // Token expired, try to refresh
    await refreshToken();
    return anonymizeData(data); // Retry with new token
  }
  
  return response.json();
}

// Token refresh function
async function refreshToken() {
  const refreshToken = localStorage.getItem('refreshToken');
  const response = await fetch('/api/v1/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken })
  });
  
  const data = await response.json();
  if (data.success) {
    localStorage.setItem('accessToken', data.accessToken);
    return data.accessToken;
  } else {
    // Refresh failed, redirect to login
    localStorage.clear();
    window.location.href = '/login';
  }
}
```

## Swagger UI Integration

The Swagger UI is now configured with JWT authentication:

1. Access Swagger UI at: `http://localhost:8080/swagger-ui.html`
2. Click the "Authorize" button
3. Enter `Bearer YOUR_JWT_TOKEN` in the value field
4. All API calls will include the authentication header

## Security Best Practices

1. **Use HTTPS in production** - Never send JWT tokens over HTTP
2. **Store tokens securely** - Use httpOnly cookies or secure storage
3. **Implement token rotation** - Regularly refresh access tokens
4. **Monitor failed login attempts** - Implement rate limiting
5. **Use strong passwords** - Enforce password complexity rules
6. **Regular security audits** - Monitor access logs and user activities

## Database Schema

The security implementation adds a `users` table:

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);
```

## Troubleshooting

### Common Issues

1. **401 Unauthorized**
   - Check if token is included in Authorization header
   - Verify token is not expired
   - Ensure token format is `Bearer {token}`

2. **403 Forbidden**
   - User doesn't have required role permissions
   - Check user role assignments

3. **Token Validation Errors**
   - Verify JWT secret key configuration
   - Check token format and signature

### Logs

Enable debug logging for security:

```properties
logging.level.com.tnc.Data.Anonymization.security=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Support

For questions or issues related to the security implementation, please contact the development team or create an issue in the project repository.
