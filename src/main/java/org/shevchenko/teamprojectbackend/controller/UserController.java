package org.shevchenko.teamprojectbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "User profile and account settings")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "User was not found")
    })
    public UserResponseDto getUserById(@Parameter(description = "User id") @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get authenticated user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public UserResponseDto getAuthenticatedUser() {
        return userService.getAuthenticatedUser();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PatchMapping("/me/email")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update authenticated user's email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password or email"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public UpdateEmailResponseDto updateEmail(
            Authentication authentication,
            @RequestBody @Valid UpdateEmailRequestDto request
    ) {
        return userService.updateEmail(authentication.getName(), request);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update authenticated user's password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid current or new password"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public MessageResponseDto updatePassword(
            Authentication authentication,
            @RequestBody @Valid UpdatePasswordRequestDto request
    ) {
        return userService.updatePassword(authentication.getName(), request);
    }
}
