package com.finanzas.backend_finanzas.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.finanzas.backend_finanzas.dto.ResumenMensualResponse;
import com.finanzas.backend_finanzas.service.ResumenMensualService;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class ResumenMensualController {

    private final ResumenMensualService resumenMensualService;


    @GetMapping("/resumen")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<?> listarTransaccionesPorUsuarioYFecha(@RequestParam Integer anio, @RequestParam Integer mes) {
        List<ResumenMensualResponse> transacciones = resumenMensualService.listarTransaccionesPorUsuarioYFecha(anio, mes);
        return ResponseEntity.ok().body(transacciones);
    }

}