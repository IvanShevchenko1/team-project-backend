package org.shevchenko.teamprojectbackend.controller;

import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.user.UserResponseDto;
import org.shevchenko.teamprojectbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
