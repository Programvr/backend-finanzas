package com.finanzas.backend_finanzas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Categorias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 50)
    private String nombre;
    
    @Column(nullable = false, length = 1)
    private String tipo;  // 'G' para gasto, 'I' para ingreso
    
    @Column(name = "icono", length = 50)
    private String icono;
    
    @Column(length = 20)
    private String color;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public boolean isValidTipo() {
        return "G".equals(tipo) || "I".equals(tipo);
    }
}