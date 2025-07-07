package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenMensualResponse{

        
        String tipo;
        BigDecimal total;
        int cantidadTransacciones;
}