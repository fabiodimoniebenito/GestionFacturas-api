package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.usuario;

import com.fabio.GestionFacturas.application.usuario.port.out.RefreshTokenRepositoryPort;
import com.fabio.GestionFacturas.domain.usuario.RefreshToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementa {@link RefreshTokenRepositoryPort} con Spring Data JPA. {@code guardar} sirve tanto
 * para crear un token (login) como para persistir una revocación (logout): el id de la entidad
 * decide si Hibernate inserta o actualiza.
 */
@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public RefreshTokenPersistenceAdapter(RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    @Override
    public RefreshToken guardar(RefreshToken refreshToken) {
        RefreshTokenJpaEntity entidad = RefreshTokenMapper.aEntidad(refreshToken);
        RefreshTokenJpaEntity guardada = refreshTokenJpaRepository.save(entidad);
        return RefreshTokenMapper.aDominio(guardada);
    }

    @Override
    public Optional<RefreshToken> buscarPorTokenHash(String tokenHash) {
        return refreshTokenJpaRepository.findByTokenHash(tokenHash).map(RefreshTokenMapper::aDominio);
    }
}
