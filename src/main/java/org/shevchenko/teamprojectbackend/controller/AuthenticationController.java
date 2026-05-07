package org.shevchenko.teamprojectbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Registration, login, and password recovery")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data"),
            @ApiResponse(responseCode = "409", description = "Email is already registered")
    })
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Log in and receive a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid login data"),
            @ApiResponse(responseCode = "401", description = "Невірна електронна пошта або пароль")
    })
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Request a password reset email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset flow accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid email")
    })
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
    @Operation(summary = "Reset password with a valid reset token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
    })
    public MessageResponseDto resetPassword(
            @RequestBody @Valid ResetPasswordRequestDto requestDto
    ) {
        authenticationService.resetPassword(requestDto);
        return new MessageResponseDto(
                "Пароль успішно скинуто"
        );
    }

}
