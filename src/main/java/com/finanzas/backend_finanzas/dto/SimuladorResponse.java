package com.finanzas.backend_finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimuladorResponse{

        BigDecimal montoObjetivo;
        BigDecimal montoActual;
        LocalDate fechaObjetivo;
        BigDecimal cuotaMensual;
}