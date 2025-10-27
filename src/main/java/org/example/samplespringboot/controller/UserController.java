package org.example.samplespringboot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.samplespringboot.dto.UserRequestDTO;
import org.example.samplespringboot.dto.UserResponseDTO;
import org.example.samplespringboot.entity.User;
import org.example.samplespringboot.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * Get current user profile
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        User authenticatedUser = getAuthenticatedUser();
        UserResponseDTO user = userService.getUserById(authenticatedUser.getId());
        return ResponseEntity.ok(user);
    }

    /**
     * Update current user profile
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        User authenticatedUser = getAuthenticatedUser();
        UserResponseDTO updatedUser = userService.updateUser(authenticatedUser.getId(), userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Helper method to get authenticated user from security context
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}


