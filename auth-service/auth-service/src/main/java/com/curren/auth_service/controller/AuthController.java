package com.curren.auth_service.controller;

import com.curren.auth_service.dto.LoginRequest;
import com.curren.auth_service.dto.TokenResponse;
import com.curren.auth_service.entity.UserEntity;
import com.curren.auth_service.repository.UserRepository;
import com.curren.auth_service.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

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

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new TokenResponse(token));
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest request) {
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(com.curren.auth_service.entity.Role.ADMIN);
        userRepository.save(user);
        return ResponseEntity.ok("Usuario creado: " + request.getEmail());
    }
}