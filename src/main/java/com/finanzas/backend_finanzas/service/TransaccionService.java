package com.finanzas.backend_finanzas.service;

import com.finanzas.backend_finanzas.dto.TransaccionRequest;
import com.finanzas.backend_finanzas.dto.TransaccionResponse;
import com.finanzas.backend_finanzas.entity.Categoria;
import com.finanzas.backend_finanzas.entity.Transaccion;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.repository.CategoriaRepository;
import com.finanzas.backend_finanzas.repository.TransaccionRepository;
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
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Transaccion registrar(TransaccionRequest request) {
        // Validaciones básicas
        validateTransactionRequest(request);
        
        // Obtener categoría
        Categoria categoria = getCategoria(request.categoria());
        
        // Obtener usuario autenticado
        Usuario usuario = getAuthenticatedUser();
        
        // Crear y guardar transacción
        return createAndSaveTransaction(request, categoria, usuario);
    }

    private void validateTransactionRequest(TransaccionRequest request) {
        if (request.monto() == null || request.monto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        
        if (request.fecha() == null || request.fecha().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la transacción no puede ser futura");
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

    private Transaccion createAndSaveTransaction(TransaccionRequest request, Categoria categoria, Usuario usuario) {
        Transaccion transaccion = Transaccion.builder()
                .usuario(usuario)
                .categoria(categoria)
                .monto(request.monto())
                .fecha(request.fecha())
                .descripcion(request.descripcion())
                .nota(request.nota())
                .recurrente(request.recurrente())
                .build();
        
        return transaccionRepository.save(transaccion);
    }

    @Transactional
    public void eliminar(Integer id) {
        Usuario usuarioId = transaccionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada con ID: " + id))
                .getUsuario();

        Usuario usuarioLogin = getAuthenticatedUser();


        if(usuarioLogin.getId() != usuarioId.getId()) {
            throw new IllegalArgumentException("Esta transacción no pertenece al usuario autenticado");
        }
        transaccionRepository.deleteById(id);
    }

    @Transactional
    public Transaccion actualizar(Integer id, TransaccionRequest request) {
        Transaccion tran = transaccionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada con ID: " + id));

        Usuario usuarioLogin = getAuthenticatedUser();

        if(usuarioLogin.getId() != tran.getUsuario().getId()) {
            throw new IllegalArgumentException("Esta transacción no pertenece al usuario autenticado");
        }
        // Validaciones básicas
        validateTransactionRequest(request);
        
        // Obtener categoría
        Categoria categoria = getCategoria(request.categoria());
        // Guardar transacción

        return updateTransaction(tran,request, categoria, usuarioLogin);
    }

    private Transaccion updateTransaction(Transaccion tran,TransaccionRequest request, Categoria categoria, Usuario usuario) {
        tran.setCategoria(categoria);
        tran.setMonto(request.monto());
        tran.setFecha(request.fecha());
        tran.setDescripcion(request.descripcion());
        tran.setNota(request.nota());
        tran.setRecurrente(request.recurrente());
        
        return transaccionRepository.save(tran);
    }

    @Transactional
    public Page<TransaccionResponse> listarTransacciones(Pageable pageable) {
        Usuario usuario = getAuthenticatedUser();
        return transaccionRepository.findByUsuarioId(usuario.getId(),pageable)
                .map(this::mapToResponse);
    }

    private TransaccionResponse mapToResponse(Transaccion transaccion) {
        return TransaccionResponse.builder()
                .id(transaccion.getId())
                .categoria(transaccion.getCategoria().getNombre())
                .tipo(transaccion.getCategoria().getTipo())
                .monto(transaccion.getMonto())
                .fecha(transaccion.getFecha())
                .descripcion(transaccion.getDescripcion())
                .nota(transaccion.getNota())
                .recurrente(transaccion.getRecurrente())
                .build();
    }
}