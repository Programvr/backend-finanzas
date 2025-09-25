package com.finanzas.backend_finanzas.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finanzas.backend_finanzas.dto.ApiResponse;
import com.finanzas.backend_finanzas.dto.ObjetivoAhorroRequest;
import com.finanzas.backend_finanzas.dto.ObjetivoAhorroResponse;
import com.finanzas.backend_finanzas.dto.SimuladorRequest;
import com.finanzas.backend_finanzas.dto.SimuladorResponse;
import com.finanzas.backend_finanzas.entity.ObjetivoAhorro;
import com.finanzas.backend_finanzas.service.ObjetivoAhorroService;

@RestController
@RequestMapping("/api/objetivoAhorro")
@RequiredArgsConstructor
public class ObjetivoAhorroController {

    private final ObjetivoAhorroService objetivoAhorroService;

    

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> register(@RequestBody ObjetivoAhorroRequest request) {
        try {
            ObjetivoAhorro objetivoAhorro = objetivoAhorroService.registrar(request);
            return ResponseEntity.ok(new ApiResponse(true, "Objetivo de ahorro registrado exitosamente", objetivoAhorro));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Integer id) {
        try {
            objetivoAhorroService.eliminar(id);
            return ResponseEntity.ok(new ApiResponse(true, "Objetivo de ahorro eliminado exitosamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Integer id, @RequestBody ObjetivoAhorroRequest request) {
        try {
            ObjetivoAhorro objetivoAhorro = objetivoAhorroService.actualizar(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Objetivo de ahorro actualizado exitosamente", objetivoAhorro));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<?> listarTransacciones(Pageable pageable) {
        Page<ObjetivoAhorroResponse> objetivosAhorro = objetivoAhorroService.listarObjetivosAhorro(pageable);
        return ResponseEntity.ok().body(objetivosAhorro);
    }

    @PostMapping("/simulador")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> simulador(@RequestBody SimuladorRequest request) {
        try {
            SimuladorResponse simulador = objetivoAhorroService.simulador(request);
            return ResponseEntity.ok(new ApiResponse(true, "Esta es su simulación: ", simulador));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

}