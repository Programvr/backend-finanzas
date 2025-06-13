package com.finanzas.backend_finanzas.dto;



public record CategoriaResponse(
        Integer id,
        String nombre,
        String tipo,
        String icono,
        String color
) {}