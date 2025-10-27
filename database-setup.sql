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

-- Insert sample data (optional)
INSERT INTO users (first_name, last_name, email, phone_number, address) VALUES
('John', 'Doe', 'john.doe@example.com', '1234567890', '123 Main St, New York, NY'),
('Jane', 'Smith', 'jane.smith@example.com', '0987654321', '456 Oak Ave, Los Angeles, CA'),
('Bob', 'Johnson', 'bob.johnson@example.com', '5551234567', '789 Pine Rd, Chicago, IL');

-- Query to view all users
SELECT * FROM users;

-- Query to find user by email
SELECT * FROM users WHERE email = 'john.doe@example.com';

-- Query to delete all data (use with caution)
-- DELETE FROM users;

-- Query to drop table (use with caution)
-- DROP TABLE IF EXISTS users;

-- Query to drop database (use with caution)
-- DROP DATABASE IF EXISTS userdb;

