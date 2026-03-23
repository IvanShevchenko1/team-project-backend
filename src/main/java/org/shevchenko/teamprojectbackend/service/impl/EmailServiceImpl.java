package org.shevchenko.teamprojectbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.shevchenko.teamprojectbackend.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password reset");
        message.setText(
                "You requested a password reset.\n\n"
                        + "Open this link to set a new password:\n"
                        + resetLink + "\n\n"
                        + "If you did not request this, ignore this email."
        );
        mailSender.send(message);
    }
}