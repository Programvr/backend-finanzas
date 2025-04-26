package com.finanzas.backend_finanzas.service;

import com.finanzas.backend_finanzas.dto.AuthRequest;
import com.finanzas.backend_finanzas.dto.AuthResponse;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
    
        var user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public void register(Usuario usuario) {
        // Verificar si el email ya está registrado
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
    
        // Codificar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    
        // Guardar el usuario en la base de datos
        usuarioRepository.save(usuario);
    }
}