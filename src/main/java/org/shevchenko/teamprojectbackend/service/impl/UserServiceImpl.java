package org.shevchenko.teamprojectbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.user.UserRegistrationRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserResponseDto;
import org.shevchenko.teamprojectbackend.exception.EntityNotFoundException;
import org.shevchenko.teamprojectbackend.exception.RegistrationException;
import org.shevchenko.teamprojectbackend.mapper.UserMapper;
import org.shevchenko.teamprojectbackend.model.Role;
import org.shevchenko.teamprojectbackend.model.User;
import org.shevchenko.teamprojectbackend.repository.RoleRepository;
import org.shevchenko.teamprojectbackend.repository.UserRepository;
import org.shevchenko.teamprojectbackend.service.UserService;
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

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    "Provided Email is already registered: " + requestDto.getEmail());
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
}
