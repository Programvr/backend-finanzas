package com.finanzas.backend_finanzas.service;

import com.finanzas.backend_finanzas.dto.CuentaRequest;
import com.finanzas.backend_finanzas.dto.CuentaResponse;
import com.finanzas.backend_finanzas.entity.Cuenta;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.repository.CuentaRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Cuenta registrar(CuentaRequest request) {
        // Validaciones básicas
        validateCuentaRequest(request);
        
        // Obtener usuario autenticado
        Usuario usuario = getAuthenticatedUser();
        
        // Crear y guardar transacción
        return createAndSaveCuenta(request, usuario);
    }

    private void validateCuentaRequest(CuentaRequest request) {
        if (request.saldoInicial() == null || request.saldoInicial().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
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

    private Cuenta createAndSaveCuenta(CuentaRequest request, Usuario usuario) {
        Cuenta cuenta = Cuenta.builder()
                .usuario(usuario)
                .nombre(request.nombre())
                .tipo(request.tipo())
                .saldoInicial(request.saldoInicial())
                .saldoActual(request.saldoActual())
                .moneda(request.moneda())
                .activa(request.activa())
                .build();
        
        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public void eliminar(Integer id) {
        Usuario usuarioId = cuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + id))
                .getUsuario();

        Usuario usuarioLogin = getAuthenticatedUser();


        if(usuarioLogin.getId() != usuarioId.getId()) {
            throw new IllegalArgumentException("Esta cuenta no pertenece al usuario autenticado");
        }
        cuentaRepository.deleteById(id);
    }

    @Transactional
    public Cuenta actualizar(Integer id, CuentaRequest request) {
        Cuenta cuen = cuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + id));

        Usuario usuarioLogin = getAuthenticatedUser();

        if(usuarioLogin.getId() != cuen.getUsuario().getId()) {
            throw new IllegalArgumentException("Esta cuenta no pertenece al usuario autenticado");
        }
        if(request.saldoActual() == null || request.saldoActual().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El saldo actual debe ser mayor o igual a cero");
        }
        // Validaciones básicas
        validateCuentaRequest(request);
        
        // Guardar transacción

        return updateCuenta(cuen,request,usuarioLogin);
    }

    private Cuenta updateCuenta(Cuenta cuen,CuentaRequest request, Usuario usuario) {
        cuen.setNombre(request.nombre());
        cuen.setTipo(request.tipo());
        cuen.setSaldoInicial(request.saldoInicial());
        cuen.setSaldoActual(request.saldoActual());
        cuen.setMoneda(request.moneda());
        cuen.setActiva(request.activa());
        
        return cuentaRepository.save(cuen);
    }

    @Transactional
    public Page<CuentaResponse> listarCuentas(Pageable pageable) {
        Usuario usuario = getAuthenticatedUser();
        return cuentaRepository.findByUsuarioId(usuario.getId(),pageable)
                .map(this::mapToResponse);
    }

    private CuentaResponse mapToResponse(Cuenta cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getId())
                .nombre(cuenta.getNombre())
                .tipo(cuenta.getTipo())
                .saldoInicial(cuenta.getSaldoInicial())
                .saldoActual(cuenta.getSaldoActual())
                .moneda(cuenta.getMoneda())
                .activa(cuenta.getActiva())
                .build();
    }

    @Transactional
    public List<CuentaResponse> consulta( ) {
        Usuario usuario = getAuthenticatedUser();
        List<Cuenta> cuenta = cuentaRepository.findByUsuarioIdAndActiva(usuario.getId(),true);
        List<CuentaResponse> cuentaResponseList = null;
        for (Cuenta c: cuenta){
             cuentaResponseList = cuenta.stream()
                .map(cuentaItem -> new CuentaResponse(
                    cuentaItem.getId(),
                    cuentaItem.getNombre(),
                    cuentaItem.getTipo(),
                    cuentaItem.getSaldoInicial(),
                    cuentaItem.getSaldoActual(),
                    cuentaItem.getMoneda(),
                    cuentaItem.getActiva()))
                .toList();
        }
        return cuentaResponseList;
    }
}