package org.shevchenko.teamprojectbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.message.MessageResponseDto;
import org.shevchenko.teamprojectbackend.dto.user.UpdateEmailRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UpdateEmailResponseDto;
import org.shevchenko.teamprojectbackend.dto.user.UpdatePasswordRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserResponseDto;
import org.shevchenko.teamprojectbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getAuthenticatedUser() {
        return userService.getAuthenticatedUser();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PatchMapping("/me/email")
    @ResponseStatus(HttpStatus.OK)
    public UpdateEmailResponseDto updateEmail(
            Authentication authentication,
            @RequestBody @Valid UpdateEmailRequestDto request
    ) {
        return userService.updateEmail(authentication.getName(), request);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDto updatePassword(
            Authentication authentication,
            @RequestBody @Valid UpdatePasswordRequestDto request
    ) {
        return userService.updatePassword(authentication.getName(), request);
    }
}
