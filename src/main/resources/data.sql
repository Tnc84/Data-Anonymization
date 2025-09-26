-- Initialize database schema and test data
-- This script runs automatically on application startup

-- Drop existing tables if they exist
DROP TABLE IF EXISTS users;

-- Create users table for authentication
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

-- Insert default users (passwords are BCrypt encoded)
-- Default password for all test users is: "password123"
INSERT INTO users (username, email, password, first_name, last_name, role, enabled) VALUES
('admin', 'admin@anonymization.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj5QN8.4N3Ey', 'System', 'Administrator', 'ADMIN', true),
('bogdan', 'bogdan@anonymization.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj5QN8.4N3Ey', 'Bogdan', 'User', 'USER', true),
('lori', 'lori@anonymization.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj5QN8.4N3Ey', 'Lori', 'User', 'USER', true),
('testuser', 'test@anonymization.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj5QN8.4N3Ey', 'Test', 'User', 'USER', true);

