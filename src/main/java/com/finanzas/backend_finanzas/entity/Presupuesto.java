package com.finanzas.backend_finanzas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Presupuestos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Presupuesto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal monto;

    @Column(columnDefinition = "Char(7) DEFAULT 'MENSUAL'")
    private String periodo;
}
