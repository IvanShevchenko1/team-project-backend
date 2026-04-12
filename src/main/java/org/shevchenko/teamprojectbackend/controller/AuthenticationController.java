package org.shevchenko.teamprojectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.message.MessageResponseDto;
import org.shevchenko.teamprojectbackend.dto.passwordResetToken.ForgotPasswordRequestDto;
import org.shevchenko.teamprojectbackend.dto.passwordResetToken.ResetPasswordRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.*;
import org.shevchenko.teamprojectbackend.exception.RegistrationException;
import org.shevchenko.teamprojectbackend.security.AuthenticationService;
import org.shevchenko.teamprojectbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDto forgotPassword(
            @RequestBody @Valid ForgotPasswordRequestDto requestDto
    ) {
        authenticationService.forgotPassword(requestDto);
        return new MessageResponseDto(
                "Якщо обліковий запис із цією електронною адресою існує, посилання для скидання паролю було надіслано"
        );
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDto resetPassword(
            @RequestBody @Valid ResetPasswordRequestDto requestDto
    ) {
        authenticationService.resetPassword(requestDto);
        return new MessageResponseDto(
                "Пароль успішно скинуто"
        );
    }

}
