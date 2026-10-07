package com.example.auth_server.service.impl;

import com.example.auth_server.dto.*;
import com.example.auth_server.entity.User;
import com.example.auth_server.entity.VerificationCode;
import com.example.auth_server.enums.VerificationCodeType;
import com.example.auth_server.exception.*;
import com.example.auth_server.repository.UserRepository;
import com.example.auth_server.repository.VerificationCodeRepository;
import com.example.auth_server.service.IAuthService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
@Validated
@AllArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private UserRepository userRepository;
    private VerificationCodeRepository verificationCodeRepository;
    private PasswordEncoder passwordEncoder;
    private EmailServiceImpl emailService;

    @Transactional
    @Override
    public void register(@Valid UserRegistrationRequest data) {
        if(userRepository.existsByEmail(data.email())) {
            throw new EmailAlreadyInUseException("Email already in use");
        }

        if(userRepository.existsByUsername(data.username())) {
            throw new UsernameAlreadyInUseException("User already exists");
        }

        User user = new User();
        user.setUsername(data.username());
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setEmail(data.email());
        user.setPfpPath("./");
        user.setEnabled(false);
        userRepository.save(user);

        sendVerificationEmail(user.getUsername(), VerificationCodeType.ACCOUNT_VERIFICATION);
    }

    @Transactional
    @Override
    public void verify(String username, String verificationCode) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuário com username: " + username + " não encontrado."));

        VerificationCode code = verificationCodeRepository
                .findFirstByUserAndTypeAndConsumedAtIsNullOrderByExpiresAtDesc(
                        user, VerificationCodeType.ACCOUNT_VERIFICATION)
                .orElseThrow(() -> new VerificationCodeNotFoundException("Invalid verification code"));

        if(code.getExpiresAt().isBefore(Instant.now())) {
            throw new ExpiredVerificationCodeException("Código de verificação expirou.");
        }

        if(!passwordEncoder.matches(verificationCode, code.getCodeHash())) {
            throw new VerificationCodeNotFoundException("Invalid verification code");
        }

        code.setConsumedAt(Instant.now());
        user.setEnabled(true);
    }

    @Transactional
    @Override
    public void sendVerificationEmail(String username, VerificationCodeType type) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        ));

        if(type != VerificationCodeType.ACCOUNT_VERIFICATION && !user.isEnabled()) {
            throw new IllegalStateException("User is not enabled");
        }

        if(verificationCodeRepository.existsByUserAndTypeAndConsumedAtIsNullAndExpiresAtAfter(
                user, type, Instant.now())) {
            throw new VerificationCodeNotFoundException(
                    "Please wait until the current " + type + " code expires before requesting another.");
        }

        String rawCode = issueCode(user, type);

        EmailTemplate template = templateFor(type);
        String htmlMessage = buildCodeEmailText(rawCode, template);

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    template.subject(),
                    htmlMessage
            );
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String issueCode(User user, VerificationCodeType type) {
        String rawCode = generateVerificationCodeValue();

        VerificationCode code = new VerificationCode();
        code.setUser(user);
        code.setType(type);
        code.setCodeHash(passwordEncoder.encode(rawCode));
        code.setExpiresAt(Instant.now().plusSeconds(900));
        verificationCodeRepository.save(code);

        return rawCode;
    }

    @Transactional
    @Override
    public void updatePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new NewPasswordMatchesCurrentPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    @Override
    public void updateUsername(String username, ChangeUsernameRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        if(userRepository.existsByUsername(request.newUsername())) {
          throw new UsernameAlreadyInUseException();
        }

        VerificationCode code = verificationCodeRepository
                .findFirstByUserAndTypeAndConsumedAtIsNullOrderByExpiresAtDesc(
                        user, VerificationCodeType.USERNAME_CHANGE)
                .orElseThrow(VerificationCodeNotFoundException::new);

        if(code.getExpiresAt().isBefore(Instant.now())) {
            throw new ExpiredVerificationCodeException("Código de verificação expirou.");
        }

        if(!passwordEncoder.matches(request.code(), code.getCodeHash())) {
            throw new VerificationCodeNotFoundException();
        }

        code.setConsumedAt(Instant.now());

        user.setUsername(request.newUsername());
    }

    @Transactional
    @Override
    public void updateEmail(String username, ChangeEmailRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        String normalizedEmail = request.newEmail().toLowerCase(Locale.ROOT);

        if(userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyInUseException();
        }

        VerificationCode code = verificationCodeRepository
                .findFirstByUserAndTypeAndConsumedAtIsNullOrderByExpiresAtDesc(
                        user, VerificationCodeType.EMAIL_CHANGE)
                .orElseThrow(VerificationCodeNotFoundException::new);

        if(code.getExpiresAt().isBefore(Instant.now())) {
            throw new ExpiredVerificationCodeException("Código de verificação expirou.");
        }

        if(!passwordEncoder.matches(request.code(), code.getCodeHash())) {
            throw new VerificationCodeNotFoundException();
        }

        code.setConsumedAt(Instant.now());
        user.setEmail(normalizedEmail);
    }

    private EmailTemplate templateFor(VerificationCodeType type) {
        return switch (type) {
            case ACCOUNT_VERIFICATION -> new EmailTemplate(
                    "Account Verification",
                    "#FFD23F",
                    "Confirme seu acesso",
                    "Digite o código abaixo para continuar no seu quadro:",
                    "VERIFICATION CODE ",
                    null,
                    "NÃO RECONHECE ESTA SOLICITAÇÃO? Ignore este email.");

            case ACCOUNT_DELETION -> new EmailTemplate(
                    "Confirmação de exclusão da conta",
                    "#FF6B6B",
                    "Excluir sua conta",
                    "Use o código abaixo para confirmar a exclusão da sua conta do Trellis Kanban:",
                    "DELETE CODE ",
                    "A exclusão da conta pode ser permanente e não poderá ser desfeita.",
                    "NÃO SOLICITOU A EXCLUSÃO? Ignore este email e não compartilhe este código.");

            case EMAIL_CHANGE -> new EmailTemplate(
                    "Confirmação de alteração de email",
                    "#FFD23F",
                    "Alterar seu email",
                    "Use o código abaixo para confirmar a alteração do seu email no Trellis Kanban:",
                    "EMAIL CODE ",
                    null,
                    "NÃO SOLICITOU ESTA ALTERAÇÃO? Ignore este email e não compartilhe este código.");

            case USERNAME_CHANGE -> new EmailTemplate(
                    "Confirmação de alteração de nome de usuário",
                    "#FFD23F",
                    "Alterar seu usuário",
                    "Use o código abaixo para confirmar a alteração do seu nome de usuário no Trellis Kanban:",
                    "USERNAME CODE ",
                    null,
                    "NÃO SOLICITOU ESTA ALTERAÇÃO? Ignore este email e não compartilhe este código.");

            case PASSWORD_CHANGE -> new EmailTemplate(
                    "Confirmação de alteração de senha",
                    "#FF6B6B",
                    "Alterar sua senha",
                    "Use o código abaixo para confirmar a alteração da sua senha no Trellis Kanban:",
                    "PASSWORD CODE ",
                    null,
                    "NÃO SOLICITOU ESTA ALTERAÇÃO? Ignore este email e não compartilhe este código.");
        };
    }

    private String buildCodeEmailText(String code, EmailTemplate t) {
        String infoBox = t.infoWarning() == null ? "" :
                "<div style=\"background-color: #FFD23F; border: 3px solid #111111; padding: 12px 16px; margin-bottom: 16px;\">"
                + "<p style=\"margin: 0; font-size: 13px; font-weight: 700; color: #111111;\">" + t.infoWarning() + "</p>"
                + "</div>";

        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, Helvetica, sans-serif; background-color: #FFF4E0; margin: 0; padding: 0;\">"
                + "<div style=\"width: 100%; max-width: 600px; margin: 0 auto; padding: 30px;\">"

                + "<div style=\"background-color: " + t.headerColor() + "; border: 4px solid #111111; padding: 24px; box-shadow: 8px 8px 0 #111111;\">"
                + "<p style=\"margin: 0; font-size: 14px; font-weight: 700; letter-spacing: 1px; text-transform: uppercase; color: #111111;\">Trellis Kanban</p>"
                + "<h1 style=\"margin: 8px 0 0; font-size: 30px; font-weight: 900; color: #111111; line-height: 1.1;\">" + t.title() + "</h1>"
                + "</div>"

                + "<div style=\"background-color: #ffffff; border: 4px solid #111111; border-top: none; padding: 28px 24px;\">"
                + "<p style=\"font-size: 16px; color: #111111; font-weight: 600; margin: 0 0 20px;\">" + t.message() + "</p>"
                + "<div style=\"background-color: #A6FAFF; border: 4px solid #111111; box-shadow: 6px 6px 0 #111111; padding: 18px; text-align: center; margin: 0 0 24px;\">"
                + "<span style=\"font-size: 30px; font-weight: 900; letter-spacing: 4px; color: #111111; font-family: 'Courier New', monospace;\">"
                + t.codeLabel() + code
                + "</span>"
                + "</div>"
                + infoBox
                + "<div style=\"background-color: #FF6B6B; border: 3px solid #111111; padding: 12px 16px;\">"
                + "<p style=\"margin: 0; font-size: 13px; font-weight: 700; color: #111111;\">" + t.ignoreWarning() + "</p>"
                + "</div>"
                + "</div>"

                + "<div style=\"background-color: #111111; border: 4px solid #111111; border-top: none; padding: 16px 24px; text-align: center;\">"
                + "<p style=\"margin: 0; font-size: 12px; font-weight: 700; color: #ffffff; text-transform: uppercase; letter-spacing: 1px;\">&copy; 2026 Trellis Kanban — Todos os direitos reservados</p>"
                + "</div>"

                + "</div>"
                + "</body>"
                + "</html>";

        return htmlMessage;
    }

    private String generateVerificationCodeValue() {
        SecureRandom random = new SecureRandom();
        String code = String.valueOf(100000 + random.nextInt(900000));

        return code;
    }
}
