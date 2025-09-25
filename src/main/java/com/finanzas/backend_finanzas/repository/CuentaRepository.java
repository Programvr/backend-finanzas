package com.finanzas.backend_finanzas.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finanzas.backend_finanzas.entity.Cuenta;




@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
    
    boolean existsById(Integer id);

    Page<Cuenta> findByUsuarioId(Integer usuarioId, Pageable pageable);

    List<Cuenta>  findByUsuarioIdAndActiva(Integer usuarioId, boolean activa);
    
}