package com.finanzas.backend_finanzas.controller;

import com.finanzas.backend_finanzas.dto.AuthRequest;
import com.finanzas.backend_finanzas.dto.AuthResponse;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Usuario usuario) {
        authService.register(usuario); // Implementa el registro en el servicio
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }
}