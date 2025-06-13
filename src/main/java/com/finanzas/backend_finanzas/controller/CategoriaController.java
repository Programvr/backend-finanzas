package com.finanzas.backend_finanzas.controller;


import com.finanzas.backend_finanzas.dto.CategoriaResponse;
import com.finanzas.backend_finanzas.service.CategoriaService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categoria")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping("/consult-categorias")
    @PreAuthorize("hasRole('USUARIO')") 
    public ResponseEntity<List<CategoriaResponse>>consultCategorias() {
        return ResponseEntity.ok(categoriaService.consultCategorias());
    }

}