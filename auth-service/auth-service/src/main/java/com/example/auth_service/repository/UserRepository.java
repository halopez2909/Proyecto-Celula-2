package com.example.auth_service.repository;

import com.example.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Esto cumple con el requisito de buscar por email
    Optional<User> findByEmail(String email);
}