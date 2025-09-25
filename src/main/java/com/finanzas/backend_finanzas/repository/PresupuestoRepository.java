package com.finanzas.backend_finanzas.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finanzas.backend_finanzas.entity.Presupuesto;




@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer> {
    
    boolean existsById(Integer id);

    Page<Presupuesto> findByUsuarioId(Integer usuarioId, Pageable pageable);

    List<Presupuesto> findByUsuarioIdAndPeriodo(Integer usuarioId, String periodo);
    
}