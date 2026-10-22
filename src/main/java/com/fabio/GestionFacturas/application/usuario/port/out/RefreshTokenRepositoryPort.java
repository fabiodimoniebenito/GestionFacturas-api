package com.fabio.GestionFacturas.application.usuario.port.out;

import com.fabio.GestionFacturas.domain.usuario.RefreshToken;

import java.util.Optional;

/** Puerto de salida implementado en infraestructura por {@code RefreshTokenPersistenceAdapter} (JPA). */
public interface RefreshTokenRepositoryPort {

    RefreshToken guardar(RefreshToken refreshToken);

    /** La búsqueda es por hash: el token en claro del cliente se hashea antes, nunca se compara ni se guarda tal cual. */
    Optional<RefreshToken> buscarPorTokenHash(String tokenHash);

}
