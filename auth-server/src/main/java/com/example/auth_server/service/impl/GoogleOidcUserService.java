package com.example.auth_server.service.impl;

import com.example.auth_server.entity.User;
import com.example.auth_server.repository.RoleRepository;
import com.example.auth_server.repository.UserRepository;
import com.example.auth_server.security.AppOidcUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GoogleOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser oidc = super.loadUser(request);

        if (oidc.getEmail() == null || !Boolean.TRUE.equals(oidc.getEmailVerified())) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_not_verified"), "Google email is missing or unverified");
        }

        String email = oidc.getEmail().toLowerCase(Locale.ROOT);
        String provider = request.getClientRegistration().getRegistrationId();

        User user = userRepository.findByEmail(email)
                .map(existing -> link(existing, provider, oidc))
                .orElseGet(() -> register(email, provider, oidc));

        return new AppOidcUser(user, oidc);
    }

    private User register(String email, String provider, OidcUser oidc) {
        User u = new User();
        u.setEmail(email);
        u.setUsername(uniqueUsername(email));
        u.setPassword(null);
        u.setPfpPath(oidc.getPicture() != null ? oidc.getPicture() : "./");
        u.setProvider(provider);
        u.setProviderId(oidc.getSubject());
        u.setEnabled(true);
        u.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(u);
    }

    private User link(User existing, String provider, OidcUser oidc) {
        if (existing.getProviderId() != null) {
            return existing; // already linked
        }

        //if an unverified local account may have been created by someone else
        // using this email, wipe its password so they cant log in through it
        if (!existing.isEnabled()) {
            existing.setPassword(null);
            existing.setEnabled(true);
        }

        existing.setProvider(provider);
        //getting the googleID
        existing.setProviderId(oidc.getSubject());
        if (oidc.getPicture() != null && "./".equals(existing.getPfpPath())) {
            existing.setPfpPath(oidc.getPicture());
        }
        return userRepository.save(existing);
    }

    private String uniqueUsername(String email) {
        String base = email.substring(0, email.indexOf('@')).replaceAll("[^A-Za-z0-9._-]", "");
        String candidate = base;
        int i = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + i++;
        }
        return candidate;
    }
}