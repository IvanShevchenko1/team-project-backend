package org.shevchenko.teamprojectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.user.UserLoginRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserLoginResponseDto;
import org.shevchenko.teamprojectbackend.dto.user.UserRegistrationRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserResponseDto;
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
    public UserResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

}
