package com.finanzas.backend_finanzas.dto;



public record AuthResponse(
        String token,
        String nombre,
        String email,
        Integer idUsuario
) {}