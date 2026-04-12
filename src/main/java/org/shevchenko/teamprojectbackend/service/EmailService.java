package org.shevchenko.teamprojectbackend.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String resetLink);
}
