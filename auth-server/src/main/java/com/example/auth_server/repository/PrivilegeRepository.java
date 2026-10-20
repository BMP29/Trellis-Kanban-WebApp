package com.example.auth_server.repository;

import com.example.auth_server.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PrivilegeRepository extends JpaRepository<Privilege, UUID> {
    Privilege findByName(String name);
}
