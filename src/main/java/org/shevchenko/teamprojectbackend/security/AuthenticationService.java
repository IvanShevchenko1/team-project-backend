package org.shevchenko.teamprojectbackend.security;

import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.user.UserLoginRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserLoginResponseDto;
import org.shevchenko.teamprojectbackend.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(),request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user.getUsername());

        return new UserLoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                token
        );
    }
}
