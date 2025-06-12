package com.finanzas.backend_finanzas.controller;

import com.finanzas.backend_finanzas.dto.*;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        try {
            Usuario usuario = authService.register(request);
            return ResponseEntity.ok(new ApiResponse(true, "Usuario registrado exitosamente", usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/update-profile")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            Usuario usuario = authService.updateProfile(request);
            return ResponseEntity.ok(new ApiResponse(true, "Perfil actualizado exitosamente", usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(request);
            return ResponseEntity.ok(new ApiResponse(true, "Contraseña cambiada exitosamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/change-roles/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> changeUserRoles(
        @PathVariable Integer userId,
        @RequestBody ChangeRoleRequest request) {
    try {
        Usuario usuario = authService.changeUserRoles(userId, request.getRoleIds());
        return ResponseEntity.ok(new ApiResponse(true, "Roles actualizados exitosamente", usuario));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage(), null));
    }
    }

    @PutMapping("/deactivate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Integer userId) {
        try {
            Usuario usuario = authService.deactivateUser(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Usuario desactivado exitosamente", usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/activate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> activateUser(@PathVariable Integer userId) {
        try {
            Usuario usuario = authService.activateUser(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Usuario activado exitosamente", usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/search-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SearchEmailResponse> searchEmail(@RequestBody SearchEmailRequest request) {
        try {
            SearchEmailResponse usuario = authService.searchEmail(request.getEmail());
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SearchEmailResponse(0,null,false, null));
        }
    }
}