package com.finanzas.backend_finanzas.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finanzas.backend_finanzas.entity.Transferencia;




@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Integer> {
    
    boolean existsById(Integer id);

    Page<Transferencia> findByUsuarioId(Integer usuarioId, Pageable pageable);

    List<Transferencia> findByUsuarioIdAndFechaBetween(Integer usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
}