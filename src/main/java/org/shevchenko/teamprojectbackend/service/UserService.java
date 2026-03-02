package org.shevchenko.teamprojectbackend.service;

import org.shevchenko.teamprojectbackend.dto.user.UserRegistrationRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserResponseDto;
import org.shevchenko.teamprojectbackend.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException;
}
