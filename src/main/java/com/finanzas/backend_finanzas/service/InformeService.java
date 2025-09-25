package com.finanzas.backend_finanzas.service;

import java.time.LocalDateTime;

public interface InformeService {
    byte[] generarInformePdf(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    byte[] generarInformeExcel(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}