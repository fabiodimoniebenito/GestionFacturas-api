package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Repositorio Spring Data; solo lo usa {@code ClientePersistenceAdapter}. */
public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, Long> {

    // Spring Data deriva la consulta del nombre del método: no hay SQL/JPQL escrito aquí
    List<ClienteJpaEntity> findByUsuarioIdAndActivoTrue(Long usuarioId);

    Optional<ClienteJpaEntity> findByUsuarioIdAndNif(Long usuarioId, String nif);
}
