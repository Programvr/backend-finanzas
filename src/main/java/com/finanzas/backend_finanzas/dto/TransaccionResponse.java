package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionResponse{

        String categoria;
        String tipo;
        BigDecimal monto;
        LocalDateTime fecha;
        String descripcion; 
        String nota; 
        boolean recurrente;
}