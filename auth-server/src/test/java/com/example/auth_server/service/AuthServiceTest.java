package com.example.auth_server.service;

import com.example.auth_server.dto.UserRegistrationRequest;
import com.example.auth_server.entity.User;
import com.example.auth_server.enums.VerificationCodeType;
import com.example.auth_server.exception.EmailAlreadyInUseException;
import com.example.auth_server.exception.UserNotFoundException;
import com.example.auth_server.exception.UsernameAlreadyInUseException;
import com.example.auth_server.repository.UserRepository;
import com.example.auth_server.repository.VerificationCodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

@SpringBootTest
public class AuthServiceTest {
    private UserRepository userRepository;
    private IAuthService authService;
    private VerificationCodeRepository codeRepository;

    @Autowired
    public AuthServiceTest(UserRepository userRepository, IAuthService authService, VerificationCodeRepository codeRepository) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.authService = authService;
    }

    @Transactional
    @Test
    public void registerTest_GeneratesVerificationCode_WhenCreatingUser() {
        final String username = "usert";
        final String password = "pwdt";
        final String email = "mail@email.com";

        UserRegistrationRequest data = new UserRegistrationRequest(username, password, email);

        this.authService.register(data);

        User user = this.userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        boolean codeWasGenerated  = this.codeRepository
                .existsByUserAndTypeAndConsumedAtIsNullAndExpiresAtAfter(user, VerificationCodeType.ACCOUNT_VERIFICATION, Instant.now());

        assertTrue(codeWasGenerated);
    }

    @Test
    @Transactional
    void register_ThrowsException_WhenEmailAlreadyExists() {
        User existingUser = new User();
        existingUser.setUsername("existing");
        existingUser.setEmail("mail@email.com");
        existingUser.setPassword("password");
        existingUser.setPfpPath("./");
        existingUser.setEnabled(false);

        userRepository.save(existingUser);

        UserRegistrationRequest data =
                new UserRegistrationRequest(
                        "newuser",
                        "password",
                        "mail@email.com"
                );

        assertThrows(
                EmailAlreadyInUseException.class,
                () -> authService.register(data)
        );
    }

    @Test
    @Transactional
    void register_ThrowsException_WhenUsernameAlreadyExists() {
        User existingUser = new User();
        existingUser.setUsername("existing");
        existingUser.setEmail("mail@email.com");
        existingUser.setPassword("password");
        existingUser.setPfpPath("./");
        existingUser.setEnabled(false);

        userRepository.save(existingUser);

        UserRegistrationRequest data =
                new UserRegistrationRequest("existing", "password", "newmail@email.com");

        assertThrows(
                UsernameAlreadyInUseException.class,
                () -> authService.register(data)
        );
    }
}
