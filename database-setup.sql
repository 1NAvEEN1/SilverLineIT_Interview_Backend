-- Database Setup Script for User Management System
-- Run this script if you want to create the database manually

-- Create database
CREATE DATABASE IF NOT EXISTS userdb;

-- Use the database
USE userdb;

-- Create users table (Spring Boot JPA will create this automatically, but this is for reference)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create user_roles table
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_role (role)
);

-- Create courses table
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(200) NOT NULL,
    course_code VARCHAR(20) UNIQUE,
    description VARCHAR(1000),
    instructor_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_instructor_id (instructor_id),
    INDEX idx_course_code (course_code)
);

-- Create course_content table
CREATE TABLE IF NOT EXISTS course_content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    file_url TEXT NOT NULL,
    course_id BIGINT NOT NULL,
    uploaded_by BIGINT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_course_id (course_id),
    INDEX idx_uploaded_by (uploaded_by)
);

-- Create refresh_tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_token (token)
);

-- Insert sample data (optional)
INSERT INTO users (first_name, last_name, email, phone_number, address) VALUES
('John', 'Doe', 'john.doe@example.com', '1234567890', '123 Main St, New York, NY'),
('Jane', 'Smith', 'jane.smith@example.com', '0987654321', '456 Oak Ave, Los Angeles, CA'),
('Bob', 'Johnson', 'bob.johnson@example.com', '5551234567', '789 Pine Rd, Chicago, IL');

-- Insert sample user roles
INSERT INTO user_roles (user_id, role) VALUES
(1, 'INSTRUCTOR'),
(2, 'STUDENT'),
(3, 'INSTRUCTOR');

-- Insert sample courses
INSERT INTO courses (course_name, course_code, description, instructor_id) VALUES
('Introduction to Java', 'CS101', 'Learn the basics of Java programming', 1),
('Advanced Database Systems', 'CS201', 'Master database design and implementation', 3);

-- Query to view all users
SELECT * FROM users;

-- Query to view all courses with instructor details
SELECT c.*, u.first_name, u.last_name
FROM courses c
JOIN users u ON c.instructor_id = u.id;

-- Query to view all course content
SELECT cc.*, c.course_name, u.first_name, u.last_name
FROM course_content cc
JOIN courses c ON cc.course_id = c.id
JOIN users u ON cc.uploaded_by = u.id;

-- Query to find user by email
SELECT * FROM users WHERE email = 'john.doe@example.com';

-- Query to find courses by instructor
SELECT * FROM courses WHERE instructor_id = 1;

-- Query to delete all data (use with caution)
-- DELETE FROM course_content;
-- DELETE FROM courses;
-- DELETE FROM user_roles;
-- DELETE FROM users;

-- Query to drop tables (use with caution)
-- DROP TABLE IF EXISTS course_content;
-- DROP TABLE IF EXISTS courses;
-- DROP TABLE IF EXISTS user_roles;
-- DROP TABLE IF EXISTS users;

-- Query to drop database (use with caution)
-- DROP DATABASE IF EXISTS userdb;

