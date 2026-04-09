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
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
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

        String token = jwtUtil.generateToken(user.getUsername());

        return new UserRegistrationResponseDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                "Реєстрація успішна",
                user.getUserCity(),
                token
        );
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
                .orElseThrow(() -> new EntityNotFoundException(
                        "Користувача з електронною поштою не знайдено: " + authenticatedEmail
                ));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Невірний пароль");
        }

        if (user.getEmail().equals(request.newEmail())) {
            throw new IllegalArgumentException("Нова електронна пошта повинна відрізнятися від поточної");
        }

        if (userRepository.existsByEmail(request.newEmail())) {
            throw new IllegalArgumentException("Ця електронна пошта вже використовується: " + request.newEmail());
        }

        user.setEmail(request.newEmail());
        userRepository.save(user);


        return new UpdateEmailResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                "Електронну пошту успішно оновлено"
        );
    }

    @Override
    public MessageResponseDto updatePassword(String authenticatedEmail, UpdatePasswordRequestDto request) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new EntityNotFoundException("Користувача не знайдено: " + authenticatedEmail));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Поточний пароль неправильний");
        }

        /*if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }*/

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Новий пароль має відрізнятися від поточного");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return new MessageResponseDto("Пароль успішно змінено");
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
