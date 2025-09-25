package com.finanzas.backend_finanzas.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finanzas.backend_finanzas.dto.ApiResponse;
import com.finanzas.backend_finanzas.dto.CuentaRequest;
import com.finanzas.backend_finanzas.dto.CuentaResponse;
import com.finanzas.backend_finanzas.entity.Cuenta;
import com.finanzas.backend_finanzas.service.CuentaService;

@RestController
@RequestMapping("/api/cuenta")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> register(@RequestBody CuentaRequest request) {
        try {
            Cuenta cuenta = cuentaService.registrar(request);
            return ResponseEntity.ok(new ApiResponse(true, "Cuenta registrada exitosamente", cuenta));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Integer id) {
        try {
            cuentaService.eliminar(id);
            return ResponseEntity.ok(new ApiResponse(true, "Cuenta eliminada exitosamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Integer id, @RequestBody CuentaRequest request) {
        try {
            Cuenta cuenta = cuentaService.actualizar(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Cuenta actualizada exitosamente", cuenta));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<?> listarCuentas(Pageable pageable) {
        Page<CuentaResponse> cuentas = cuentaService.listarCuentas(pageable);
        return ResponseEntity.ok().body(cuentas);
    }

    @GetMapping("/consulta")
    @PreAuthorize("hasRole('USUARIO')") 
    public ResponseEntity<List<CuentaResponse>>consulta() {
        return ResponseEntity.ok(cuentaService.consulta());
    }

}