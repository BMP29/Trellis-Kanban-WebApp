package com.example.auth_server.service.impl;

import com.example.auth_server.dto.ChangePasswordRequest;
import com.example.auth_server.dto.EmailTemplate;
import com.example.auth_server.dto.UserRegistrationRequest;
import com.example.auth_server.dto.VerifyUserRequestDto;
import com.example.auth_server.entity.User;
import com.example.auth_server.entity.VerificationCode;
import com.example.auth_server.enums.VerificationCodeType;
import com.example.auth_server.exception.*;
import com.example.auth_server.repository.UserRepository;
import com.example.auth_server.repository.VerificationCodeRepository;
import com.example.auth_server.service.IAuthService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private UserRepository userRepository;
    private VerificationCodeRepository verificationCodeRepository;
    private PasswordEncoder passwordEncoder;
    private EmailServiceImpl emailService;


    @Override
    public void register(UserRegistrationRequest data) {
        User user = new User();

        if(userRepository.existsByEmail(data.email())) {
            throw new RuntimeException("Email already in use");
        }

        if(userRepository.existsByUsername(data.username())) {
            throw new RuntimeException("User already exists");
        }

        String verificationCode = generateVerificationCode();

        user.setUsername(data.username());
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setEmail(data.email());
        user.setPfpPath("./");
        user.setEnabled(false);
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiresAt(Instant.now().plusSeconds(900));
        user.setDeleteAccountCodeExpiresAt(null);
        user.setDeleteAccountCode(null);
        sendCodeEmail(user, VerificationCodeType.ACCOUNT_VERIFICATION, verificationCode);

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void verify(VerifyUserRequestDto data) {
        User user = userRepository.findByEmail(data.email())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o e-mail: " + data.email()));

        if(user.getVerificationCodeExpiresAt().isBefore(Instant.now())) {
            throw new ExpiredVerificationCodeException("Código de verificação expirou.");
        }

        if (!Objects.equals(data.verificationCode(), user.getVerificationCode())) {
            throw new InvalidVerificationCodeException("Invalid verification code");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
    }


    @Transactional
    @Override
    public void sendVerificationEmail(User user, VerificationCodeType type) {
        if (!user.isEnabled()) {
            throw new IllegalStateException("User is not enabled");
        }

        if(verificationCodeRepository
                .existsByUserAndType(user, type)) {
            throw new InvalidVerificationCodeException(
                    "Please wait until the current " + type + " code expires before requesting another."
            );
        }

        String rawCode = generateVerificationCode();
        VerificationCode code = new VerificationCode();

        code.setUser(user);
        code.setExpiresAt(Instant.now().plusSeconds(900));
        code.setType(type);
        code.setCodeHash(passwordEncoder.encode(rawCode));

        sendCodeEmail(user, type, rawCode);
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
            throw new IncorrectCurrentPasswordException();
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new NewPasswordMatchesCurrentPasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    @Override
    public void updateUsername(UUID userId, String newUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if(userRepository.existsByUsername(newUsername)) {
          throw new UsernameAlreadyInUseException();
        }

        user.setUsername(newUsername);
    }


    //delete in the future
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
    public void updateEmail(UUID userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String normalizedEmail = newEmail.toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyInUseException();
        }

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

    private String buildCodeEmailText(VerificationCodeType type, String code, EmailTemplate t) {
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
