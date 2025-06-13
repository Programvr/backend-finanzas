package com.finanzas.backend_finanzas.service;



import com.finanzas.backend_finanzas.dto.CategoriaResponse;
import com.finanzas.backend_finanzas.entity.Categoria;
import com.finanzas.backend_finanzas.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional
    public List<CategoriaResponse> consultCategorias( ) {
        List<Categoria> categoria = categoriaRepository.findAll();
        List<CategoriaResponse> categoriaResponseList = null;
        for (Categoria c: categoria){
             categoriaResponseList = categoria.stream()
                .map(categoriaItem -> new CategoriaResponse(
                    categoriaItem.getId(),
                    categoriaItem.getNombre(),
                    categoriaItem.getTipo(),
                    categoriaItem.getIcono(),
                    categoriaItem.getColor()))
                .toList();
        }
        return categoriaResponseList;
    }

    
}