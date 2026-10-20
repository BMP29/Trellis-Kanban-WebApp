package com.example.auth_server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "username", updatable = true, nullable = false, unique = true)
    private String username;
    @Column(name = "password")
    private String password;
    @NotBlank
    @Column(name = "email", updatable = true, nullable = false, unique = true)
    private String email;
    @Column(name = "pfp_path")
    private String pfpPath;

    @NotNull
    @Column
    private boolean enabled = false;

    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant lastUpdateAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        if(roles != null) {
            for(Role role : roles) {
               authorities.add(
                       new SimpleGrantedAuthority(role.getName())
               );

               if(role.getPrivileges() != null) {
                   for(Privilege privilege : role.getPrivileges()) {
                       authorities.add(
                               new SimpleGrantedAuthority(privilege.getName())
                       );
                   }
               }
            }
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
