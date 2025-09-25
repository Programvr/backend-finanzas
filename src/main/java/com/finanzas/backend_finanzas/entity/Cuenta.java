package com.finanzas.backend_finanzas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Cuentas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(columnDefinition = "Nvarchar(200)", nullable = false)
    private String nombre;

    @Column(columnDefinition = "Nvarchar(100)", nullable = false)
    private String tipo;

    @Column(name = "saldo_inicial", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "saldo_actual", nullable = false, precision = 18, scale = 2)
    private BigDecimal saldoActual;

    @Column(columnDefinition = "Char(3) DEFAULT 'COP'")
    private String moneda;
    
    @Column(columnDefinition = "BIT DEFAULT 1")
    private Boolean activa;
}
