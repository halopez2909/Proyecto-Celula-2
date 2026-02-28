package com.reto.auth.controller;

import com.reto.auth.dto.LoginRequest;
import com.reto.auth.dto.RegisterRequest;
import com.reto.auth.dto.TokenResponse;
import com.reto.auth.entity.Role;
import com.reto.auth.entity.UserEntity;
import com.reto.auth.repository.UserRepository;
import com.reto.auth.security.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * POST /auth/register
     * Registra un nuevo usuario.
     * Body: { "email": "...", "password": "...", "role": "ADMIN" | "CUSTOMER" }
     * Si no se envía role, por defecto es CUSTOMER.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        // Verificar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El email ya está registrado"));
        }

        // Determinar el rol
        Role role = Role.CUSTOMER;
        if (request.getRole() != null) {
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Rol inválido. Use ADMIN o CUSTOMER"));
            }
        }

        // Crear y guardar usuario
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        userRepository.save(user);

        log.info("[Auth] Usuario registrado: {} con rol: {}", request.getEmail(), role);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Usuario creado exitosamente",
                        "email", request.getEmail(),
                        "role", role.name()
                ));
    }

    /**
     * POST /auth/login
     * Autentica usuario y retorna JWT.
     * Body: { "email": "...", "password": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        // Credenciales inválidas → 401
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("[Auth] Login fallido para: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        String token = jwtService.generateToken(user);
        log.info("[Auth] Login exitoso: {} ({})", user.getEmail(), user.getRole());

        return ResponseEntity.ok(new TokenResponse(token, user.getEmail(), user.getRole().name()));
    }
}
