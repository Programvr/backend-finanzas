package com.finanzas.backend_finanzas.service;


import com.finanzas.backend_finanzas.dto.RolResponse;
import com.finanzas.backend_finanzas.entity.Rol;
import com.finanzas.backend_finanzas.repository.RolRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    @Transactional
    public List<RolResponse> consultRoles( ) {
        List<Rol> rol = rolRepository.findAll();
        List<RolResponse> rolResponseList = null;
        for (Rol r: rol){
             rolResponseList = rol.stream()
                .map(rolItem -> new RolResponse(
                    rolItem.getId(),
                    rolItem.getNombre(),
                    rolItem.getDescripcion()))
                .toList();
        }
        return rolResponseList;
    }

    
}