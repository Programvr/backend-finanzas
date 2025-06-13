package com.finanzas.backend_finanzas.service;

import com.finanzas.backend_finanzas.dto.*;
import com.finanzas.backend_finanzas.entity.Rol;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.exception.ResourceNotFoundException;
import com.finanzas.backend_finanzas.repository.RolRepository;
import com.finanzas.backend_finanzas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        var user = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!user.isActivo()) { 
            throw new DisabledException("Usuario inactivo. Contacte al administrador.");
        }
        
        // Actualizar último login
        user.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(user);
        
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken, user.getNombre(), user.getEmail(), user.getId());
    }

    @Transactional
    public Usuario register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        Rol userRole = rolRepository.findByEsPredeterminadoTrue()
                .orElseThrow(() -> new IllegalStateException("Rol USER no encontrado"));
        
        var user = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fechaRegistro(LocalDateTime.now())
                .activo(true)
                .roles(Collections.singleton(userRole))
                .build();
        
        return usuarioRepository.save(user);
    }

    @Transactional
    public Usuario updateProfile(UpdateProfileRequest request) {
        Usuario user = usuarioRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        if (request.nombre() != null && !request.nombre().isEmpty()) {
            user.setNombre(request.nombre());
        }
        
        if (request.email() != null && !request.email().isEmpty() && !request.email().equals(user.getEmail())) {
            if (usuarioRepository.existsByEmail(request.email())) {
                throw new IllegalArgumentException("El nuevo email ya está en uso");
            }
            user.setEmail(request.email());
        }
        
        return usuarioRepository.save(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Usuario user = usuarioRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        
        if (request.newPassword().equals(request.currentPassword())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");
        }
        
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        usuarioRepository.save(user);
    }

    @Transactional
    public Usuario changeUserRoles(Integer userId, List<Integer> roleIds) {
    Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    
            System.out.println("Usuario encontrado: " + usuario.getEmail());
    // Limpiar roles existentes
    usuario.getRoles().clear();
    System.out.println("Roles existentes limpiados para el usuario: " + usuario.getEmail());
    
    // Buscar y asignar nuevos roles
    List<Rol> nuevosRoles = rolRepository.findAllById(roleIds);
    if (nuevosRoles.size() != roleIds.size()) {
        throw new IllegalArgumentException("Algunos roles no existen");
    }
    
    usuario.getRoles().addAll(nuevosRoles);
    return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario deactivateUser(Integer userId) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        if (!user.isActivo()) {
            throw new IllegalArgumentException("El usuario ya está desactivado");
        }
        
        user.setActivo(false);
        return usuarioRepository.save(user);
    }

    @Transactional
    public Usuario activateUser(Integer userId) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        if (user.isActivo()) {
            throw new IllegalArgumentException("El usuario ya está activo");
        }
        
        user.setActivo(true);
        return usuarioRepository.save(user);
    }
    @Transactional
    public SearchEmailResponse searchEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        List<Integer> roleIds = new ArrayList<>();
        for (Rol rol : usuario.getRoles()) {
            roleIds.add(rol.getId());
        }
        SearchEmailResponse response = new SearchEmailResponse(usuario.getId(),usuario.getEmail(),usuario.isActivo(),roleIds);
        return response;
    }
}