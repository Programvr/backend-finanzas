package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SimuladorRequest(BigDecimal montoObjetivo, BigDecimal montoActual, LocalDate fechaObjetivo, BigDecimal cuotaMensual ) {}
