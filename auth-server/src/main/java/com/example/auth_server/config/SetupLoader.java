package com.example.auth_server.config;

import com.example.auth_server.entity.Privilege;
import com.example.auth_server.entity.Role;
import com.example.auth_server.entity.User;
import com.example.auth_server.repository.PrivilegeRepository;
import com.example.auth_server.repository.RoleRepository;
import com.example.auth_server.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class SetupLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Privilege readPrivilege =
                createPrivilegeIfNotFound("READ_PRIVILEGE");

        Privilege writePrivilege =
                createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        createRoleIfNotFound(
                "ROLE_ADMIN",
                Arrays.asList(readPrivilege, writePrivilege)
        );

        createRoleIfNotFound(
                "ROLE_USER",
                Arrays.asList(readPrivilege)
        );

        if (userRepository.findByEmail("test@test.com").isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");

            User user = new User();
            user.setUsername("Test");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setRoles(Arrays.asList(adminRole));
            user.setEnabled(true);

            userRepository.save(user);
        }
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege();

            privilege.setName(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role();

            role.setName(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}