# ✅ User Entity Refactoring Complete

## Changes Summary

### 🎯 What Was Done

1. **Removed UserDetails Implementation**
   - Removed `implements UserDetails` from User entity
   - Removed all UserDetails override methods
   - Removed Spring Security imports

2. **Changed User-Role Relationship**
   - Changed from `@ManyToMany` (user can have multiple roles) to `@OneToOne` (user has one role)
   - Removed `user_roles` junction table
   - Now using direct foreign key `role_id` in users table

3. **Updated User Entity**
   - Added `password` field (String, required)
   - Changed from `List<UserRole> roles` to single `UserRole role`
   - Removed `enabled` field
   - Removed all UserDetails methods

---

## 📋 Updated Files

### 1. User.java (Entity)
**Before**:
```java
public class User implements UserDetails {
    private List<UserRole> roles;
    private boolean enabled = true;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { ... }
    // ... other UserDetails methods
}
```

**After**:
```java
public class User {
    @Column(nullable = false)
    private String password;
    
    @OneToOne
    @JoinColumn(name = "role_id")
    private UserRole role;
}
```

### 2. UserRole.java (Entity)
**Structure** (unchanged):
```java
@Entity
@Table(name = "roles")
public class UserRole {
    private Long id;
    private String name; // STUDENT, INSTRUCTOR, ADMIN
}
```

### 3. UserRequestDTO.java
**Added**:
- `password` field (required, 6-255 characters)
- `roleId` field (optional, Long)

### 4. UserResponseDTO.java
**Added**:
- `roleId` field (Long)
- `roleName` field (String)

### 5. UserServiceImpl.java
**Updated**:
- Added `UserRoleRepository` dependency
- `mapToEntity()` now handles password and role
- `mapToDTO()` now includes role information
- `updateUser()` can update password and role

### 6. UserRoleRepository.java
**Added method**:
```java
Optional<UserRole> findByName(String name);
```

### 7. database-setup.sql
**Changed**:
- Created `roles` table (instead of `user_roles` junction table)
- Added `password` and `role_id` columns to `users` table
- Removed `user_roles` junction table
- Added sample roles: STUDENT, INSTRUCTOR, ADMIN

---

## 🗄️ Database Schema Changes

### New Structure

```sql
-- Roles table (master data)
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Users table (with role foreign key)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- NEW
    phone_number VARCHAR(20),
    address VARCHAR(255),
    role_id BIGINT,  -- NEW (foreign key to roles)
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

### Migration Script

If you have an existing database, run:

```sql
-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('STUDENT'), ('INSTRUCTOR'), ('ADMIN');

-- Add new columns to users table
ALTER TABLE users 
ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT 'changeme',
ADD COLUMN role_id BIGINT,
ADD FOREIGN KEY (role_id) REFERENCES roles(id);

-- Drop old user_roles table if it exists
DROP TABLE IF EXISTS user_roles;
```

---

## 📝 User-Role Relationship

### Before (Many-to-Many)
- User could have multiple roles
- Required junction table `user_roles`
- Complex queries needed

### After (One-to-One)
- User has exactly one role
- Direct foreign key in users table
- Simple queries

---

## 🔧 API Changes

### Create User

**Request**:
```json
POST /api/users
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123",
    "phoneNumber": "1234567890",
    "address": "123 Main St",
    "roleId": 1  // Optional: ID of role (1=STUDENT, 2=INSTRUCTOR, 3=ADMIN)
}
```

**Response**:
```json
{
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phoneNumber": "1234567890",
    "address": "123 Main St",
    "roleId": 1,
    "roleName": "STUDENT",
    "createdAt": "2025-10-27T10:00:00",
    "updatedAt": "2025-10-27T10:00:00"
}
```

### Update User

**Request**:
```json
PUT /api/users/1
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "newpassword123",  // Optional: omit if not changing
    "roleId": 2  // Optional: change role to INSTRUCTOR
}
```

---

## ✅ Benefits

1. **Simpler Model**: One role per user is clearer and easier to manage
2. **Better Performance**: Direct foreign key, no junction table joins
3. **Easier Queries**: `user.getRole()` instead of `user.getRoles().get(0)`
4. **No Security Dependency**: Removed Spring Security dependency from entity layer
5. **Clean Separation**: Security concerns separated from domain model

---

## ⚠️ Important Notes

### Password Storage
Currently passwords are stored as **plain text** for development purposes.

**For Production**:
```java
// Add Spring Security BCrypt
@Service
public class UserServiceImpl {
    private final PasswordEncoder passwordEncoder;
    
    private User mapToEntity(UserRequestDTO dto) {
        // ...
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        // ...
    }
}
```

### Default Roles
The system comes with 3 default roles:
- `STUDENT` (id: 1)
- `INSTRUCTOR` (id: 2)
- `ADMIN` (id: 3)

### Null Role Handling
If `roleId` is not provided during user creation, the user will have `role = null`. You may want to set a default role:

```java
// In UserServiceImpl.mapToEntity()
if (dto.getRoleId() == null) {
    // Default to STUDENT role
    UserRole defaultRole = userRoleRepository.findByName("STUDENT")
            .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
    user.setRole(defaultRole);
}
```

---

## 🧪 Testing

### Test User Creation
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "password": "password123",
    "roleId": 1
  }'
```

### Test User Update (Change Role)
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "roleId": 2
  }'
```

### Verify User Response
```bash
curl http://localhost:8080/api/users/1
```

Expected response includes `roleId` and `roleName`.

---

## 📊 Summary

✅ **Removed**: UserDetails implementation  
✅ **Removed**: All UserDetails override methods  
✅ **Removed**: ManyToMany roles relationship  
✅ **Removed**: `user_roles` junction table  
✅ **Removed**: `enabled` field  
✅ **Added**: `password` field  
✅ **Changed**: To OneToOne role relationship  
✅ **Updated**: DTOs to include role information  
✅ **Updated**: Service layer to handle role assignment  
✅ **Updated**: Database schema  

**Status**: ✅ Complete and Ready to Use

---

**Date**: October 27, 2025  
**Version**: Updated User Model

