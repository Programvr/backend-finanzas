package com.finanzas.backend_finanzas.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finanzas.backend_finanzas.dto.ApiResponse;
import com.finanzas.backend_finanzas.dto.PresupuestoRequest;
import com.finanzas.backend_finanzas.dto.PresupuestoResponse;
import com.finanzas.backend_finanzas.entity.Presupuesto;
import com.finanzas.backend_finanzas.service.PresupuestoService;

@RestController
@RequestMapping("/api/presupuesto")
@RequiredArgsConstructor
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> register(@RequestBody PresupuestoRequest request) {
        try {
            Presupuesto presupuesto = presupuestoService.registrar(request);
            return ResponseEntity.ok(new ApiResponse(true, "Presupuesto registrado exitosamente", presupuesto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Integer id) {
        try {
            presupuestoService.eliminar(id);
            return ResponseEntity.ok(new ApiResponse(true, "Presupuesto eliminado exitosamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Integer id, @RequestBody PresupuestoRequest request) {
        try {
            Presupuesto presupuesto = presupuestoService.actualizar(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Presupuesto actualizado exitosamente", presupuesto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<?> listarPresupuestos(Pageable pageable) {
        Page<PresupuestoResponse> presupuestos = presupuestoService.listarPresupuestos(pageable);
        return ResponseEntity.ok().body(presupuestos);
    }

}