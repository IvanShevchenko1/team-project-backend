package org.shevchenko.teamprojectbackend.security;

import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.dto.passwordResetToken.ForgotPasswordRequestDto;
import org.shevchenko.teamprojectbackend.dto.passwordResetToken.ResetPasswordRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserLoginRequestDto;
import org.shevchenko.teamprojectbackend.dto.user.UserLoginResponseDto;
import org.shevchenko.teamprojectbackend.model.PasswordResetToken;
import org.shevchenko.teamprojectbackend.model.User;
import org.shevchenko.teamprojectbackend.repository.PasswordResetTokenRepository;
import org.shevchenko.teamprojectbackend.repository.UserRepository;
import org.shevchenko.teamprojectbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenUtil passwordResetTokenUtil;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordBaseUrl;

    @Value("${app.password-reset.expiration-minutes}")
    private long resetPasswordExpirationMinutes;

    public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(),request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user.getId());

        return new UserLoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                token
        );
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto requestDto) {
        userRepository.findByEmail(requestDto.email()).ifPresent(user -> {
            passwordResetTokenRepository.deleteByUser(user);

            String rawToken = passwordResetTokenUtil.generateRawToken();
            String tokenHash = passwordResetTokenUtil.hashToken(rawToken);

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setTokenHash(tokenHash);
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(resetPasswordExpirationMinutes));
            resetToken.setUsed(false);

            passwordResetTokenRepository.save(resetToken);

            String resetLink = resetPasswordBaseUrl + "?token=" + rawToken;
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto requestDto) {
        /*if (!requestDto.newPassword().equals(requestDto.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }*/

        String tokenHash = passwordResetTokenUtil.hashToken(requestDto.token());

        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Reset token has already been used");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(requestDto.newPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}
