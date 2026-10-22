package com.fabio.GestionFacturas.application.usuario.service;

import com.fabio.GestionFacturas.application.usuario.port.in.RegistrarUsuarioUseCase;
import com.fabio.GestionFacturas.application.usuario.port.out.UsuarioRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.EmailYaRegistradoException;
import com.fabio.GestionFacturas.domain.usuario.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementa {@link RegistrarUsuarioUseCase} (registro), invocado desde {@code AuthController}.
 * Hashea la contraseña con {@code PasswordEncoder} (BCrypt) antes de construir el {@link Usuario} de dominio:
 * la contraseña en claro no sale de este service.
 */
@Service
public class RegistrarUsuarioService implements RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrarUsuarioService(UsuarioRepositoryPort usuarioRepository,
                                   PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Usuario registrar(ComandoRegistrar comando) {
        // La unicidad se comprueba aquí (no solo con la restricción de BD) para lanzar una excepción de dominio clara -> HTTP 409
        usuarioRepository.buscarPorEmail(comando.email()).ifPresent(u -> {
            throw new EmailYaRegistradoException("Ya existe un usuario con ese email");
        });

        String hash = passwordEncoder.encode(comando.password());

        Usuario usuario = new Usuario(null, comando.email(), comando.nombre(), hash);

        return usuarioRepository.guardar(usuario);
    }
}