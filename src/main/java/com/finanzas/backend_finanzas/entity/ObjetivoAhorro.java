package com.finanzas.backend_finanzas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ObjetivosAhorro")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjetivoAhorro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(length = 200)
    private String nombre;

    @Column(name = "monto_objetivo", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoObjetivo;

    @Column(name = "monto_actual", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoActual;

    @Column(name = "fecha_objetivo", nullable = false)
    private LocalDate fechaObjetivo;

    @Column(nullable = false)
    private boolean completado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_asociada_id", nullable = false)
    private Cuenta cuenta;

}
