package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresupuestoResponse{

        Integer id;
        String categoria;
        String tipo;
        BigDecimal monto;
        String periodo;
}