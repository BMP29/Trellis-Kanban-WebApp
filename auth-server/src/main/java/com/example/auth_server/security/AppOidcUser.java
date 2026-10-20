package com.example.auth_server.security;

import com.example.auth_server.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.*;

@Getter
public class AppOidcUser extends DefaultOidcUser {
    private final User user;

    public AppOidcUser(User user, OidcUser google) {
        super(withFactor(user.getAuthorities()), google.getIdToken(), google.getUserInfo());
        this.user = user;
    }

    private static Collection<GrantedAuthority> withFactor(Collection<? extends GrantedAuthority> base) {
        List<GrantedAuthority> all = new ArrayList<>(base);
        all.add(FactorGrantedAuthority.fromAuthority(FactorGrantedAuthority.AUTHORIZATION_CODE_AUTHORITY));
        return all;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getName() { return user.getUsername(); }
}