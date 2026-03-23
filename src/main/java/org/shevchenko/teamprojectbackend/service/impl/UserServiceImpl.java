package org.shevchenko.teamprojectbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.message.MessageResponseDto;
import org.shevchenko.teamprojectbackend.dto.user.*;
import org.shevchenko.teamprojectbackend.exception.EntityNotFoundException;
import org.shevchenko.teamprojectbackend.exception.RegistrationException;
import org.shevchenko.teamprojectbackend.mapper.UserMapper;
import org.shevchenko.teamprojectbackend.model.Role;
import org.shevchenko.teamprojectbackend.model.User;
import org.shevchenko.teamprojectbackend.repository.RoleRepository;
import org.shevchenko.teamprojectbackend.repository.UserRepository;
import org.shevchenko.teamprojectbackend.security.JwtUtil;
import org.shevchenko.teamprojectbackend.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    "Email вже зареєстровано: " + requestDto.getEmail());
        }

        if (userRepository.existsByName(requestDto.getName())) {
            throw new RegistrationException(
                    "Username вже зареєстровано: " + requestDto.getName());
        }

        User user = userMapper.toModel(requestDto);

        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Role userRole = roleRepository.findByRole(Role.RoleName.USER)
                .orElseThrow(() -> new IllegalStateException(
                        "Default role USER is missing. Add roles first."));
        user.getRoles().add(userRole);
        user.setAccountStatus(User.UserStatus.active);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id: " + id)
        );
        return userMapper.toDto(user);
    }

    @Override
    public UpdateEmailResponseDto updateEmail(String authenticatedEmail, UpdateEmailRequestDto request) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + authenticatedEmail));

        if (!user.getEmail().equals(request.currentEmail())) {
            throw new IllegalArgumentException("Current email does not match authenticated user");
        }

        if (request.currentEmail().equals(request.newEmail())) {
            throw new IllegalArgumentException("New email must be different from current email");
        }

        if (userRepository.existsByEmail(request.newEmail())) {
            throw new IllegalArgumentException("Email is already in use: " + request.newEmail());
        }

        user.setEmail(request.newEmail());
        userRepository.save(user);

        String newToken = jwtUtil.generateToken(user.getUsername());

        return new UpdateEmailResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                newToken,
                "Email updated successfully"
        );
    }

    @Override
    public MessageResponseDto updatePassword(String authenticatedEmail, UpdatePasswordRequestDto request) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + authenticatedEmail));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        /*if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }*/

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return new MessageResponseDto("Password updated successfully");
    }

    @Override
    public UserResponseDto getAuthenticatedUser() {
        User user = getAuthenticatedUserOrThrow();
        return userMapper.toDto(user);
    }

    @Override
    public User getAuthenticatedUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new AccessDeniedException("Authenticated principal is invalid");
        }

        return user;
    }
}
