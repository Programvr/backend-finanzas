package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;

public record CuentaRequest(String nombre, String tipo, BigDecimal saldoInicial, BigDecimal saldoActual, String moneda, boolean activa ) {}
