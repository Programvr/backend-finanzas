package com.finanzas.backend_finanzas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Roles")
public class Rol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    private String descripcion;
    
    @Column(name = "es_predeterminado")
    private boolean esPredeterminado;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "RolePermisos",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();
    
    public void agregarPermiso(Permiso permiso) {
        this.permisos.add(permiso);
    }
    
    public void removerPermiso(Permiso permiso) {
        this.permisos.remove(permiso);
    }
}