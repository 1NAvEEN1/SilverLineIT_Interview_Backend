package org.example.samplespringboot.service;

import lombok.RequiredArgsConstructor;
import org.example.samplespringboot.dto.AuthResponseDTO;
import org.example.samplespringboot.dto.LoginRequestDTO;
import org.example.samplespringboot.dto.RegisterRequestDTO;
import org.example.samplespringboot.entity.RefreshToken;
import org.example.samplespringboot.entity.User;
import org.example.samplespringboot.entity.UserRole;
import org.example.samplespringboot.exception.DuplicateResourceException;
import org.example.samplespringboot.exception.ResourceNotFoundException;
import org.example.samplespringboot.repository.UserRepository;
import org.example.samplespringboot.repository.UserRoleRepository;
import org.example.samplespringboot.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        // Assign role (single role)
        if (request.getRoleId() != null) {
            UserRole role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));
            user.setRole(role);
        } else {
            // Default role is INSTRUCTOR (id: 2)
            UserRole defaultRole = roleRepository.findById(2L)
                    .orElseThrow(() -> new ResourceNotFoundException("Default INSTRUCTOR role not found"));
            user.setRole(defaultRole);
        }

        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(savedUser.getEmail(), savedUser.getId(), savedUser.getRole().getName());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .build();
    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Generate tokens
        String accessToken = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole().getName());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    @Transactional
    public AuthResponseDTO refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr);

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole().getName());

        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenService.revokeToken(refreshTokenStr);
    }
}

