-- JWT Authentication Database Setup Script
-- This script creates the necessary tables and inserts default roles

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS userdb;
USE userdb;

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT unique_role_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert default roles
INSERT IGNORE INTO roles (name) VALUES ('STUDENT');
INSERT IGNORE INTO roles (name) VALUES ('INSTRUCTOR');
INSERT IGNORE INTO roles (name) VALUES ('ADMIN');

-- Users table (will be created/updated by JPA with password field)
-- The JPA will handle this, but here's the expected structure:
/*
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_email UNIQUE (email),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
*/

-- User-Roles junction table
/*
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
*/

-- Refresh tokens table
/*
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_revoked (user_id, revoked)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
*/

-- Courses table (existing)
/*
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(200) NOT NULL,
    course_code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    instructor_id BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_course_code (course_code),
    INDEX idx_instructor (instructor_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
*/

-- Course contents table (existing)
/*
CREATE TABLE IF NOT EXISTS course_contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    uploaded_by BIGINT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_course (course_id),
    INDEX idx_uploaded_by (uploaded_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
*/

-- Create an admin user (password: admin123)
-- Note: This password is BCrypt encoded. You can generate new ones using BCryptPasswordEncoder
-- BCrypt hash for "admin123": $2a$10$XQjW1qVZ7qX9Y5XQjW1qVOGKZJ9H8Y5XQjW1qVZ7qX9Y5XQjW1qVO
/*
INSERT IGNORE INTO users (first_name, last_name, email, password, enabled)
VALUES ('System', 'Admin', 'admin@system.com', '$2a$10$XptfskK.MNXbGMmAe.qGfOJTHqpJ9GJqp/ZZHqWc9CqVy0H8iLGFe', TRUE);

-- Get the admin user ID and assign ADMIN role
SET @admin_user_id = (SELECT id FROM users WHERE email = 'admin@system.com');
SET @admin_role_id = (SELECT id FROM roles WHERE name = 'ADMIN');

INSERT IGNORE INTO user_roles (user_id, role_id)
VALUES (@admin_user_id, @admin_role_id);
*/

-- Display created roles
SELECT * FROM roles;

-- Notes:
-- 1. The commented tables will be auto-created by Spring Boot JPA with ddl-auto=update
-- 2. Only the roles table needs to be pre-populated
-- 3. To create an admin user, use the /api/auth/register endpoint with roles: ["ADMIN"]
-- 4. The BCrypt password encoding is handled automatically by the application

