package com.finanzas.backend_finanzas.service;


import com.finanzas.backend_finanzas.dto.ObjetivoAhorroRequest;
import com.finanzas.backend_finanzas.dto.ObjetivoAhorroResponse;
import com.finanzas.backend_finanzas.dto.SimuladorRequest;
import com.finanzas.backend_finanzas.dto.SimuladorResponse;
import com.finanzas.backend_finanzas.entity.Cuenta;
import com.finanzas.backend_finanzas.entity.ObjetivoAhorro;
import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.repository.CuentaRepository;
import com.finanzas.backend_finanzas.repository.ObjetivoAhorroRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ObjetivoAhorroService {

    private final UsuarioRepository usuarioRepository;
    private final CuentaRepository cuentaRepository;
    private final ObjetivoAhorroRepository objetivoAhorroRepository;

    @Transactional
    public ObjetivoAhorro registrar(ObjetivoAhorroRequest request) {
        // Validaciones básicas
        validateObjetivoAhorroRequest(request);

        // Obtener usuario autenticado
        Usuario usuario = getAuthenticatedUser();
        
        // Obtener cuenta
        Cuenta cuenta = getCuenta(request.cuenta());
        
        // Crear y guardar transacción
        return createAndSaveObjetivoAhorro(request, cuenta, usuario);
    }

    private void validateObjetivoAhorroRequest(ObjetivoAhorroRequest request) {
        if (request.nombre() == null || request.nombre().equals("")) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        
        if (request.montoObjetivo() == null || request.montoObjetivo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto objetivo debe ser mayor que cero");
        }
        
        if (request.fechaObjetivo() == null || request.fechaObjetivo().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha objetivo debe ser futura");
        }

        if (request.cuenta() == null) {
            throw new IllegalArgumentException("La cuenta asociada es requerida");
        }
    }

    private Cuenta getCuenta(Integer cuentaId) {
        return cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con ID: " + cuentaId));
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

    private ObjetivoAhorro createAndSaveObjetivoAhorro(ObjetivoAhorroRequest request, Cuenta cuenta, Usuario usuario) {
        ObjetivoAhorro objetivoAhorro = ObjetivoAhorro.builder()
                .usuario(usuario)
                .nombre(request.nombre())
                .montoObjetivo(request.montoObjetivo())
                .montoActual(request.montoActual())
                .fechaObjetivo(request.fechaObjetivo())
                .completado(request.completado())
                .cuenta(cuenta)
                .build();
        
        return objetivoAhorroRepository.save(objetivoAhorro);
    }

    @Transactional
    public void eliminar(Integer id) {
        Usuario usuarioId = objetivoAhorroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Objetivo de ahorro no encontrado con ID: " + id))
                .getUsuario();

        Usuario usuarioLogin = getAuthenticatedUser();


        if(usuarioLogin.getId() != usuarioId.getId()) {
            throw new IllegalArgumentException("Este objetivo de ahorro no pertenece al usuario autenticado");
        }
        objetivoAhorroRepository.deleteById(id);
    }

    @Transactional
    public ObjetivoAhorro actualizar(Integer id, ObjetivoAhorroRequest request) {
        ObjetivoAhorro obj = objetivoAhorroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Objetivo de ahorro no encontrado con ID: " + id));

        Usuario usuarioLogin = getAuthenticatedUser();

        if(usuarioLogin.getId() != obj.getUsuario().getId()) {
            throw new IllegalArgumentException("Este objetivo de ahorro no pertenece al usuario autenticado");
        }
        // Validaciones básicas
        validateObjetivoAhorroRequest(request);
        
        // Obtener cuenta
        Cuenta cuenta = getCuenta(request.cuenta());
        // Guardar transacción

        return updateObjetivoAhorro(obj,request, cuenta, usuarioLogin);
    }

    private ObjetivoAhorro updateObjetivoAhorro(ObjetivoAhorro obj,ObjetivoAhorroRequest request, Cuenta cuenta, Usuario usuario) {
        obj.setNombre(request.nombre());
        obj.setMontoObjetivo(request.montoObjetivo());
        obj.setMontoActual(request.montoActual());
        obj.setFechaObjetivo(request.fechaObjetivo());
        obj.setCompletado(request.completado());
        obj.setCuenta(cuenta);
        
        return objetivoAhorroRepository.save(obj);
    }

    @Transactional
    public Page<ObjetivoAhorroResponse> listarObjetivosAhorro(Pageable pageable) {
        Usuario usuario = getAuthenticatedUser();
        return objetivoAhorroRepository.findByUsuarioId(usuario.getId(),pageable)
                .map(this::mapToResponse);
    }

    private ObjetivoAhorroResponse mapToResponse(ObjetivoAhorro objetivoAhorro) {
        return ObjetivoAhorroResponse.builder()
                .id(objetivoAhorro.getId())
                .nombre(objetivoAhorro.getNombre())
                .montoObjetivo(objetivoAhorro.getMontoObjetivo())
                .montoActual(objetivoAhorro.getMontoActual())
                .fechaObjetivo(objetivoAhorro.getFechaObjetivo())
                .completado(objetivoAhorro.isCompletado())
                .Cuenta(objetivoAhorro.getCuenta().getNombre())
                .build();
    }

    @Transactional
    public SimuladorResponse simulador(SimuladorRequest request) {

        SimuladorResponse response = null;

        LocalDate fechaActual = LocalDateTime.now().toLocalDate();

        validateSimuladorRequest(request);

        if (request.montoObjetivo() == null || request.montoObjetivo().compareTo(BigDecimal.ZERO) <= 0){
            
            int meses = diferenciaMeses(request.fechaObjetivo(), fechaActual);

            BigDecimal total = request.cuotaMensual()
                .multiply(BigDecimal.valueOf(meses))
                .add(request.montoActual());

            response = SimuladorResponse.builder()
                .montoObjetivo(total)
                .montoActual(request.montoActual())
                .fechaObjetivo(request.fechaObjetivo())
                .cuotaMensual(request.cuotaMensual())
                .build();
        }else if(request.cuotaMensual() == null || request.cuotaMensual().compareTo(BigDecimal.ZERO) <= 0){
            
            int meses = diferenciaMeses(request.fechaObjetivo(), fechaActual);

            BigDecimal subtotal = request.montoObjetivo().subtract(request.montoActual());

            BigDecimal total = subtotal
                .divide(BigDecimal.valueOf(meses));

                response = SimuladorResponse.builder()
                .montoObjetivo(request.montoObjetivo())
                .montoActual(request.montoActual())
                .fechaObjetivo(request.fechaObjetivo())
                .cuotaMensual(total)
                .build();

        }else if(request.fechaObjetivo() == null || request.fechaObjetivo().isBefore(LocalDate.now())){

            BigDecimal subtotal = request.montoObjetivo().subtract(request.montoActual());

            int meses = subtotal.divide(request.cuotaMensual()).intValue();

            LocalDate fechaFin = fechaActual.plusMonths(meses);

                response = SimuladorResponse.builder()
                .montoObjetivo(request.montoObjetivo())
                .montoActual(request.montoActual())
                .fechaObjetivo(fechaFin)
                .cuotaMensual(request.cuotaMensual())
                .build();

        }
        return  response;
    }

    private void validateSimuladorRequest(SimuladorRequest request) {
        if (request.montoActual() == null || request.montoActual().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto actual no puede estar vacio");
        }
        
        if ((request.montoObjetivo() == null || request.montoObjetivo().compareTo(BigDecimal.ZERO) < 0) && (request.fechaObjetivo() == null || request.fechaObjetivo().isBefore(LocalDate.now()))) {
            throw new IllegalArgumentException("El monto objetivo debe ser mayor que cero o la fecha objetivo debe ser futura");
        }

        if ((request.montoObjetivo() == null || request.montoObjetivo().compareTo(BigDecimal.ZERO) < 0) && (request.cuotaMensual() == null || request.cuotaMensual().compareTo(BigDecimal.ZERO) < 0)) {
            throw new IllegalArgumentException("El monto objetivo debe ser mayor que cero o la cuota mensual debe ser mayor que cero");
        }
        
        if ((request.fechaObjetivo() == null || request.fechaObjetivo().isBefore(LocalDate.now())) && (request.cuotaMensual() == null || request.cuotaMensual().compareTo(BigDecimal.ZERO) < 0)) {
            throw new IllegalArgumentException("La fecha objetivo debe ser futura o la cuota mensual debe ser mayor que cero");
        }

    }

    public static int diferenciaMeses(LocalDate fecha1, LocalDate fecha2) {
        // Asegurar que fecha1 sea la más antigua
        if (fecha1.isAfter(fecha2)) {
            LocalDate temp = fecha1;
            fecha1 = fecha2;
            fecha2 = temp;
        }
        
        long meses = ChronoUnit.MONTHS.between(
            fecha1.withDayOfMonth(1), 
            fecha2.withDayOfMonth(1)
        );
        
        return (int) meses;
    }
    
}