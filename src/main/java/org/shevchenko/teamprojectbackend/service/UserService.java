package org.shevchenko.teamprojectbackend.service;

import org.shevchenko.teamprojectbackend.dto.message.MessageResponseDto;
import org.shevchenko.teamprojectbackend.dto.user.UpdateEmailRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UpdateEmailResponseDto;
import org.shevchenko.teamprojectbackend.dto.user.UpdatePasswordRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserRegistrationRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserResponseDto;
import org.shevchenko.teamprojectbackend.exception.RegistrationException;
import org.shevchenko.teamprojectbackend.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException;

    UserResponseDto getUserById(Long id);

    UpdateEmailResponseDto updateEmail(String authenticatedEmail, UpdateEmailRequestDto request);

    MessageResponseDto updatePassword(String authenticatedEmail, UpdatePasswordRequestDto request);

    UserResponseDto getAuthenticatedUser();

    User getAuthenticatedUserOrThrow();
}
