package com.finanzas.backend_finanzas.controller;



import com.finanzas.backend_finanzas.dto.RolResponse;
import com.finanzas.backend_finanzas.service.RolService;

import lombok.RequiredArgsConstructor;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rol")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping("/consult-roles")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<List<RolResponse>>consultRoles() {
        return ResponseEntity.ok(rolService.consultRoles());
    }

}