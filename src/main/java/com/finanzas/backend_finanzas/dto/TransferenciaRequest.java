package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenciaRequest(Integer cuentaOrigen, Integer cuentaDestino, BigDecimal monto, LocalDateTime fecha, String descripcion ) {}
