package com.finanzas.backend_finanzas.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.finanzas.backend_finanzas.entity.ResumenMensual;




@Repository
public interface ResumenMensualRepository extends JpaRepository<ResumenMensual, Integer> {
    
    
    List<ResumenMensual> findByUsuarioIdAndAnioAndMes(Integer usuarioId, Integer anio, Integer mes);
    
}