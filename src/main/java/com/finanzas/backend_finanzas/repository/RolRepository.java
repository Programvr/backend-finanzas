package com.finanzas.backend_finanzas.repository;

import com.finanzas.backend_finanzas.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Buscar rol por nombre
    Optional<Rol> findByNombre(String nombre);

    // Buscar rol predeterminado
    Optional<Rol> findByEsPredeterminadoTrue();

    // Verificar si existe un rol con nombre específico (excluyendo un ID dado)
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Rol r WHERE r.nombre = :nombre AND r.id != :excludeId")
    boolean existsByNombreAndIdNot(String nombre, Integer excludeId);

    // Verificar si existe un rol con nombre específico
    boolean existsByNombre(String nombre);
}