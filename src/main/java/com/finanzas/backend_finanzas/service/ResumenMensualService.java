package com.finanzas.backend_finanzas.service;

import com.finanzas.backend_finanzas.dto.ResumenMensualResponse;
import com.finanzas.backend_finanzas.entity.ResumenMensual;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.repository.ResumenMensualRepository;
import com.finanzas.backend_finanzas.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumenMensualService {

    private final ResumenMensualRepository resumenMensualRepository;
    private final UsuarioRepository usuarioRepository;

    
    @Transactional
    public List<ResumenMensualResponse> listarTransaccionesPorUsuarioYFecha(Integer anio, Integer mes) {
    Usuario usuario = getAuthenticatedUser();
    List<ResumenMensual> resumenes = resumenMensualRepository.findByUsuarioIdAndAnioAndMes(usuario.getId(), anio, mes);
    return resumenes.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private ResumenMensualResponse mapToResponse(ResumenMensual resumenMensual) {
    return ResumenMensualResponse.builder()
            .tipo(resumenMensual.getTipo())
            .total(resumenMensual.getTotal())
            .cantidadTransacciones(resumenMensual.getCantidadTransacciones())
            .build();
    }

    private Usuario getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Usuario no autenticado");
        }
        
        String username = authentication.getName();
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));
    }
    
    
}