package com.finanzas.backend_finanzas.controller;

import com.finanzas.backend_finanzas.service.InformeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/informes")
@RequiredArgsConstructor
public class InformeController {

    private final InformeService informeService;

    @GetMapping("/descargar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<InputStreamResource> descargarInforme(
            @RequestParam String tipo, // "pdf" o "excel"
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin
    ) {
        byte[] data;
        String filename;
        MediaType mediaType;

        fechaInicio += "T00:00:00";
        fechaFin += "T23:59:59";

        if ("pdf".equalsIgnoreCase(tipo)) {
            data = informeService.generarInformePdf(LocalDateTime.parse(fechaInicio), LocalDateTime.parse(fechaFin));
            filename = "informe.pdf";
            mediaType = MediaType.APPLICATION_PDF;
        } else if ("excel".equalsIgnoreCase(tipo)) {
            data = informeService.generarInformeExcel(LocalDateTime.parse(fechaInicio), LocalDateTime.parse(fechaFin));
            filename = "informe.xlsx";
            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(new InputStreamResource(new ByteArrayInputStream(data)));
    }
}