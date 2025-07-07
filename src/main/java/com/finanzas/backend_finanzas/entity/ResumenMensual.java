package com.finanzas.backend_finanzas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ResumenMensual")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenMensual {
    
    @Id
    @Column(name = "id")
    private Long id;  
    
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;
    
    @Column(nullable = false)
    private Integer anio;
    
    @Column(nullable = false)
    private Integer mes;
    
    @Column(nullable = false)
    private String tipo;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal total;
    
    @Column(name = "cantidad_transacciones", nullable = false)
    private Integer cantidadTransacciones;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;
}