package com.finanzas.backend_finanzas.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Este es un endpoint público";
    }

    @GetMapping("/private")
    @PreAuthorize("hasRole('USER')")
    public String privateEndpoint() {
        return "Este es un endpoint privado";
    }
}