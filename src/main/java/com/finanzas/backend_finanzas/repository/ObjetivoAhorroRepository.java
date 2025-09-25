package com.finanzas.backend_finanzas.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finanzas.backend_finanzas.entity.ObjetivoAhorro;


@Repository
public interface ObjetivoAhorroRepository extends JpaRepository<ObjetivoAhorro, Integer> {
    
    Page<ObjetivoAhorro> findByUsuarioId(Integer usuarioId, Pageable pageable);
}