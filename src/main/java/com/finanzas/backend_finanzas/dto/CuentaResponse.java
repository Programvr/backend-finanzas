package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaResponse{

        Integer id;
        String nombre;
        String tipo;
        BigDecimal saldoInicial;
        BigDecimal saldoActual;
        String moneda;
        Boolean activa;
}