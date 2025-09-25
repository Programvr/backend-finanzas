package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferenciaResponse{

        Integer id;
        String cuentaOrigen;
        String cuentaDestino;
        BigDecimal monto;
        LocalDateTime fecha;
        String descripcion;
}