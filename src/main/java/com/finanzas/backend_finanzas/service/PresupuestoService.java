package com.finanzas.backend_finanzas.service;

import com.finanzas.backend_finanzas.dto.PresupuestoRequest;
import com.finanzas.backend_finanzas.dto.PresupuestoResponse;
import com.finanzas.backend_finanzas.entity.Categoria;
import com.finanzas.backend_finanzas.entity.Presupuesto;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.repository.CategoriaRepository;
import com.finanzas.backend_finanzas.repository.PresupuestoRepository;
import com.finanzas.backend_finanzas.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Presupuesto registrar(PresupuestoRequest request) {
        // Validaciones básicas
        validatePresupuestoRequest(request);
        
        // Obtener categoría
        Categoria categoria = getCategoria(request.categoria());
        
        // Obtener usuario autenticado
        Usuario usuario = getAuthenticatedUser();
        
        // Crear y guardar transacción
        return createAndSavePresupuesto(request, categoria, usuario);
    }

    private void validatePresupuestoRequest(PresupuestoRequest request) {
        if (request.monto() == null || request.monto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        
        if (request.categoria() == null) {
            throw new IllegalArgumentException("La categoría es requerida");
        }
    }

    private Categoria getCategoria(Integer categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + categoriaId));
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

    private Presupuesto createAndSavePresupuesto(PresupuestoRequest request, Categoria categoria, Usuario usuario) {
        Presupuesto presupuesto = Presupuesto.builder()
                .usuario(usuario)
                .categoria(categoria)
                .monto(request.monto())
                .periodo(request.periodo())
                .build();
        
        return presupuestoRepository.save(presupuesto);
    }

    @Transactional
    public void eliminar(Integer id) {
        Usuario usuarioId = presupuestoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Presupuesto no encontrado con ID: " + id))
                .getUsuario();

        Usuario usuarioLogin = getAuthenticatedUser();


        if(usuarioLogin.getId() != usuarioId.getId()) {
            throw new IllegalArgumentException("Este presupuesto no pertenece al usuario autenticado");
        }
        presupuestoRepository.deleteById(id);
    }

    @Transactional
    public Presupuesto actualizar(Integer id, PresupuestoRequest request) {
        Presupuesto presu = presupuestoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Presupuesto no encontrado con ID: " + id));

        Usuario usuarioLogin = getAuthenticatedUser();

        if(usuarioLogin.getId() != presu.getUsuario().getId()) {
            throw new IllegalArgumentException("Este presupuesto no pertenece al usuario autenticado");
        }
        // Validaciones básicas
        validatePresupuestoRequest(request);
        
        // Obtener categoría
        Categoria categoria = getCategoria(request.categoria());
        // Guardar transacción

        return updatePresupuesto(presu,request, categoria, usuarioLogin);
    }

    private Presupuesto updatePresupuesto(Presupuesto presu,PresupuestoRequest request, Categoria categoria, Usuario usuario) {
        presu.setCategoria(categoria);
        presu.setMonto(request.monto());
        presu.setPeriodo(request.periodo());
        
        return presupuestoRepository.save(presu);
    }

    @Transactional
    public Page<PresupuestoResponse> listarPresupuestos(Pageable pageable) {
        Usuario usuario = getAuthenticatedUser();
        return presupuestoRepository.findByUsuarioId(usuario.getId(),pageable)
                .map(this::mapToResponse);
    }

    private PresupuestoResponse mapToResponse(Presupuesto presupuesto) {
        return PresupuestoResponse.builder()
                .id(presupuesto.getId())
                .categoria(presupuesto.getCategoria().getNombre())
                .tipo(presupuesto.getCategoria().getTipo())
                .monto(presupuesto.getMonto())
                .periodo(presupuesto.getPeriodo())
                .build();
    }
}