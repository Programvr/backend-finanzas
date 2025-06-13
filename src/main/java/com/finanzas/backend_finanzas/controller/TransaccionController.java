package com.finanzas.backend_finanzas.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finanzas.backend_finanzas.dto.ApiResponse;
import com.finanzas.backend_finanzas.dto.TransaccionRequest;
import com.finanzas.backend_finanzas.dto.TransaccionResponse;
import com.finanzas.backend_finanzas.entity.Transaccion;
import com.finanzas.backend_finanzas.service.TransaccionService;

@RestController
@RequestMapping("/api/transaccion")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;

    

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> register(@RequestBody TransaccionRequest request) {
        try {
            Transaccion transaccion = transaccionService.registrar(request);
            return ResponseEntity.ok(new ApiResponse(true, "Transacción registrada exitosamente", transaccion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Integer id) {
        try {
            transaccionService.eliminar(id);
            return ResponseEntity.ok(new ApiResponse(true, "Transacción eliminada exitosamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Integer id, @RequestBody TransaccionRequest request) {
        try {
            Transaccion transaccion = transaccionService.actualizar(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Transacción actualizada exitosamente", transaccion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<?> listarTransacciones(Pageable pageable) {
        Page<TransaccionResponse> transacciones = transaccionService.listarTransacciones(pageable);
        return ResponseEntity.ok().body(transacciones);
    }

}