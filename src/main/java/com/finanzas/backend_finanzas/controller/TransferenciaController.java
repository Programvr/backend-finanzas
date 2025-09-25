package com.finanzas.backend_finanzas.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finanzas.backend_finanzas.dto.ApiResponse;
import com.finanzas.backend_finanzas.dto.TransferenciaRequest;
import com.finanzas.backend_finanzas.dto.TransferenciaResponse;
import com.finanzas.backend_finanzas.entity.Transferencia;
import com.finanzas.backend_finanzas.service.TransferenciaService;

@RestController
@RequestMapping("/api/transferencia")
@RequiredArgsConstructor
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> register(@RequestBody TransferenciaRequest request) {
        try {
            Transferencia transferencia = transferenciaService.registrar(request);
            return ResponseEntity.ok(new ApiResponse(true, "Transferencia registrada exitosamente", transferencia));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Integer id) {
        try {
            transferenciaService.eliminar(id);
            return ResponseEntity.ok(new ApiResponse(true, "Transferencia eliminada exitosamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Integer id, @RequestBody TransferenciaRequest request) {
        try {
            Transferencia transferencia = transferenciaService.actualizar(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Transferencia actualizada exitosamente", transferencia));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<?> listarTransferencias(Pageable pageable) {
        Page<TransferenciaResponse> transferencias = transferenciaService.listarTransferencias(pageable);
        return ResponseEntity.ok().body(transferencias);
    }

}