package com.example.auth_server.service.impl;

import com.example.auth_server.dto.UserDto;
import com.example.auth_server.entity.User;
import com.example.auth_server.exception.ExpiredVerificationCodeException;
import com.example.auth_server.repository.UserRepository;
import com.example.auth_server.service.IUserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;


@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private UserRepository userRepository;
    private EmailServiceImpl emailService;

    @Override
    public UserDto getUser() {
        return null;
    }

    @Override
    public UserDto updateUser() {
        return null;
    }

    @Override
    public void sendDeletionCode(User user) {
        if (!user.isEnabled()) {
            throw new IllegalStateException("User is not enabled");
        }

        Instant expiresAt = user.getDeleteAccountCodeExpiresAt();

        if (expiresAt != null && expiresAt.isAfter(Instant.now())) {
            throw new RuntimeException(
                    "Please wait until the current code expires before requesting another."
            );
        }

        SecureRandom random = new SecureRandom();
        String code = String.valueOf(100000 + random.nextInt(900000));

        user.setDeleteAccountCode(code);
        user.setDeleteAccountCodeExpiresAt(
                Instant.now().plusSeconds(900)
        );

        sendAccountDeletionEmail(user);
    }

    @Transactional
    @Override
    public void deleteUser(User user, String code) {
        Instant expiresAt = user.getDeleteAccountCodeExpiresAt();

        if (expiresAt == null || expiresAt.isBefore(Instant.now())) {
            throw new ExpiredVerificationCodeException("Code has expired.");
        }

        if(!Objects.equals(user.getDeleteAccountCode(), code)) {
            throw new RuntimeException("Incorrect code");
        }

        this.userRepository.delete(user);
    }

    private void sendAccountDeletionEmail(User user) {
        String subject = "Confirmação de exclusão da conta";
        String deletionCode = "DELETE CODE " + user.getDeleteAccountCode();

        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, Helvetica, sans-serif; background-color: #FFF4E0; margin: 0; padding: 0;\">"
                + "<div style=\"width: 100%; max-width: 600px; margin: 0 auto; padding: 30px;\">"

                + "<div style=\"background-color: #FF6B6B; border: 4px solid #111111; padding: 24px; box-shadow: 8px 8px 0 #111111;\">"
                + "<p style=\"margin: 0; font-size: 14px; font-weight: 700; letter-spacing: 1px; text-transform: uppercase; color: #111111;\">Trellis Kanban</p>"
                + "<h1 style=\"margin: 8px 0 0; font-size: 30px; font-weight: 900; color: #111111; line-height: 1.1;\">Excluir sua conta</h1>"
                + "</div>"

                + "<div style=\"background-color: #ffffff; border: 4px solid #111111; border-top: none; padding: 28px 24px;\">"
                + "<p style=\"font-size: 16px; color: #111111; font-weight: 600; margin: 0 0 20px;\">"
                + "Use o código abaixo para confirmar a exclusão da sua conta do Trellis Kanban:"
                + "</p>"

                + "<div style=\"background-color: #A6FAFF; border: 4px solid #111111; box-shadow: 6px 6px 0 #111111; padding: 18px; text-align: center; margin: 0 0 24px;\">"
                + "<span style=\"font-size: 30px; font-weight: 900; letter-spacing: 4px; color: #111111; font-family: 'Courier New', monospace;\">"
                + deletionCode
                + "</span>"
                + "</div>"

                + "<div style=\"background-color: #FFD23F; border: 3px solid #111111; padding: 12px 16px; margin-bottom: 16px;\">"
                + "<p style=\"margin: 0; font-size: 13px; font-weight: 700; color: #111111;\">"
                + "⚠ A exclusão da conta pode ser permanente e não poderá ser desfeita."
                + "</p>"
                + "</div>"

                + "<div style=\"background-color: #FF6B6B; border: 3px solid #111111; padding: 12px 16px;\">"
                + "<p style=\"margin: 0; font-size: 13px; font-weight: 700; color: #111111;\">"
                + "⚠ NÃO SOLICITOU A EXCLUSÃO? Ignore este email e não compartilhe este código."
                + "</p>"
                + "</div>"

                + "</div>"

                + "<div style=\"background-color: #111111; border: 4px solid #111111; border-top: none; padding: 16px 24px; text-align: center;\">"
                + "<p style=\"margin: 0; font-size: 12px; font-weight: 700; color: #ffffff; text-transform: uppercase; letter-spacing: 1px;\">"
                + "&copy; 2026 Trellis Kanban — Todos os direitos reservados"
                + "</p>"
                + "</div>"

                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
