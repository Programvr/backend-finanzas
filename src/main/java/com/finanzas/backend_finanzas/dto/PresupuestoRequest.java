package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;

public record PresupuestoRequest(Integer categoria,BigDecimal monto, String periodo ) {}
