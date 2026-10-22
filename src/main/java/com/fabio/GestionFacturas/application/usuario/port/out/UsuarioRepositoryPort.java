package com.fabio.GestionFacturas.application.usuario.port.out;

import com.fabio.GestionFacturas.domain.usuario.Usuario;

import java.util.Optional;

/** Puerto de salida implementado en infraestructura por {@code UsuarioPersistenceAdapter} (JPA). */
public interface UsuarioRepositoryPort {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

}
