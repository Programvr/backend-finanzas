package com.finanzas.backend_finanzas.service;

import com.finanzas.backend_finanzas.entity.Usuario;
import com.finanzas.backend_finanzas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Convertir los roles del usuario a GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .collect(Collectors.toList());

        // Crear UserDetails con los datos del usuario
        return new User(
                usuario.getEmail(),       // Username (en este caso, el email)
                usuario.getPassword(),   // Password (ya encriptado)
                usuario.isActivo(),     // Cuenta activa
                true,                   // Cuenta no expirada
                true,                   // Credenciales no expiradas
                true,                   // Cuenta no bloqueada
                authorities             // Roles convertidos
        );
    }
}