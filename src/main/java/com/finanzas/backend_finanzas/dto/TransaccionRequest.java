package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransaccionRequest(Integer categoria,BigDecimal monto, LocalDateTime fecha, String descripcion, String nota, boolean recurrente) {}
