package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ObjetivoAhorroRequest(String nombre, BigDecimal montoObjetivo, BigDecimal montoActual, LocalDate fechaObjetivo, boolean completado, Integer cuenta ) {}
