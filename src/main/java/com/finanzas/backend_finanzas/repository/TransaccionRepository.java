package com.finanzas.backend_finanzas.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finanzas.backend_finanzas.entity.Transaccion;




@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    
    boolean existsById(Integer id);

    Page<Transaccion> findByUsuarioId(Integer usuarioId, Pageable pageable);

    List<Transaccion> findByUsuarioIdAndFechaBetween(Integer usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
}