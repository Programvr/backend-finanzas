package com.finanzas.backend_finanzas.service;

import com.finanzas.backend_finanzas.dto.CuentaRequest;
import com.finanzas.backend_finanzas.dto.TransferenciaRequest;
import com.finanzas.backend_finanzas.dto.TransferenciaResponse;
import com.finanzas.backend_finanzas.entity.Cuenta;
import com.finanzas.backend_finanzas.entity.Transferencia;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.repository.CuentaRepository;
import com.finanzas.backend_finanzas.repository.TransferenciaRepository;
import com.finanzas.backend_finanzas.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.*;

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
public class TransferenciaService {

    private final TransferenciaRepository transferenciaRepository;
    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuentaService cuentaService;

    @Transactional
    public Transferencia registrar(TransferenciaRequest request) {
        // Validaciones básicas
        validateTransferenciaRequest(request);

        // Obtener cuentaOrigen y cuentaDestino
        Cuenta cuentaOrigen = getCuenta(request.cuentaOrigen());
        Cuenta cuentaDestino = getCuenta(request.cuentaDestino());
        
        // Obtener usuario autenticado
        Usuario usuario = getAuthenticatedUser();

        updateCuenta(
            request.cuentaOrigen(),
            request.cuentaDestino(),
            cuentaOrigen,
            cuentaDestino,
            request.monto()
        );
        
        // Crear y guardar transferencia
        return createAndSaveTransferencia(request,cuentaOrigen, cuentaDestino, usuario);

    }

    private void validateTransferenciaRequest(TransferenciaRequest request) {
        if (request.monto() == null || request.monto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        
        if (request.fecha() == null || request.fecha().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la transferencia no puede ser futura");
        }
    }

    private Cuenta getCuenta(Integer cuentaId) {
        return cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + cuentaId));
    }

    private void updateCuenta(Integer idOrigen, Integer idDestino, Cuenta cuentaOrigen, Cuenta cuentaDestino, BigDecimal monto) {
        CuentaRequest cuentaOrigenRequest = new CuentaRequest(
            cuentaOrigen.getNombre(),
            cuentaOrigen.getTipo(),
            cuentaOrigen.getSaldoInicial(),
            cuentaOrigen.getSaldoActual().subtract(monto).max(BigDecimal.ZERO),
            cuentaOrigen.getMoneda(),
            cuentaOrigen.getActiva()
        );

        Cuenta cuentaOrigenActualiza = cuentaService.actualizar(idOrigen, cuentaOrigenRequest);

        CuentaRequest cuentaDestinoRequest = new CuentaRequest(
            cuentaDestino.getNombre(),
            cuentaDestino.getTipo(),
            cuentaDestino.getSaldoInicial(),
            cuentaDestino.getSaldoActual().add(monto),
            cuentaDestino.getMoneda(),
            cuentaDestino.getActiva()
        );

        Cuenta cuentaDestinoActualiza = cuentaService.actualizar(idDestino, cuentaDestinoRequest);
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

    private Transferencia createAndSaveTransferencia(TransferenciaRequest request, Cuenta cuentaOrigen, Cuenta cuentaDestino, Usuario usuario) {
        Transferencia transferencia = Transferencia.builder()
                .usuario(usuario)
                .cuentaOrigen(cuentaOrigen)
                .cuentaDestino(cuentaDestino)
                .monto(request.monto())
                .fecha(request.fecha())
                .descripcion(request.descripcion())
                .build();
        
        return transferenciaRepository.save(transferencia);
    }

    @Transactional
    public void eliminar(Integer id) {
        Transferencia transferencia = transferenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada con ID: " + id));

        Usuario usuarioLogin = getAuthenticatedUser();

        // Obtener cuentaOrigen y cuentaDestino
        Cuenta cuentaOrigen = getCuenta(transferencia.getCuentaOrigen().getId());
        Cuenta cuentaDestino = getCuenta(transferencia.getCuentaDestino().getId());

        updateCuenta(
            transferencia.getCuentaDestino().getId(),
            transferencia.getCuentaOrigen().getId(),
            cuentaDestino,
            cuentaOrigen,
            transferencia.getMonto()
        );


        if(usuarioLogin.getId() != transferencia.getUsuario().getId()) {
            throw new IllegalArgumentException("Esta transferencia no pertenece al usuario autenticado");
        }
        transferenciaRepository.deleteById(id);
    }

    @Transactional
    public Transferencia actualizar(Integer id, TransferenciaRequest request) {
        Transferencia tran = transferenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada con ID: " + id));

        Usuario usuarioLogin = getAuthenticatedUser();

        if(usuarioLogin.getId() != tran.getUsuario().getId()) {
            throw new IllegalArgumentException("Esta transferencia no pertenece al usuario autenticado");
        }
        // Validaciones básicas
        validateTransferenciaRequest(request);
        
        // Obtener cuentaOrigen y cuentaDestino
        Cuenta cuentaOrigen = getCuenta(request.cuentaOrigen());
        Cuenta cuentaDestino = getCuenta(request.cuentaDestino());

        BigDecimal montoActual = request.monto().subtract(tran.getMonto());

        updateCuenta(
            request.cuentaOrigen(),
            request.cuentaDestino(),
            cuentaOrigen,
            cuentaDestino,
            montoActual
        );

        // Guardar transacción

        return updateTransferencia(tran,request, cuentaOrigen, cuentaDestino, usuarioLogin);
    }

    private Transferencia updateTransferencia(Transferencia tran,TransferenciaRequest request, Cuenta cuentaOrigen, Cuenta cuentaDestino, Usuario usuario) {
        tran.setCuentaOrigen(cuentaOrigen);
        tran.setCuentaDestino(cuentaDestino);
        tran.setMonto(request.monto());
        tran.setFecha(request.fecha());
        tran.setDescripcion(request.descripcion());
        
        return transferenciaRepository.save(tran);
    }

    @Transactional
    public Page<TransferenciaResponse> listarTransferencias(Pageable pageable) {
        Usuario usuario = getAuthenticatedUser();
        return transferenciaRepository.findByUsuarioId(usuario.getId(),pageable)
                .map(this::mapToResponse);
    }

    private TransferenciaResponse mapToResponse(Transferencia transferencia) {
        return TransferenciaResponse.builder()
                .id(transferencia.getId())
                .cuentaOrigen(transferencia.getCuentaOrigen().getNombre())
                .cuentaDestino(transferencia.getCuentaDestino().getNombre())
                .monto(transferencia.getMonto())
                .fecha(transferencia.getFecha())
                .descripcion(transferencia.getDescripcion())
                .build();
    }
}