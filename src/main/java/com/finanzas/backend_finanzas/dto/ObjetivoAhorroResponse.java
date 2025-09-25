package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjetivoAhorroResponse{

        Integer id;
        String nombre;
        BigDecimal montoObjetivo;
        BigDecimal montoActual;
        LocalDate fechaObjetivo;
        boolean completado;
        String Cuenta;
}