package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/** Repositorio Spring Data; el CRUD viene de {@code JpaRepository}. Solo lo usa {@code GastoPersistenceAdapter}. */
public interface GastoJpaRepository extends JpaRepository<GastoJpaEntity, Long> {

    // Spring Data deriva la consulta del nombre del método: no hay SQL/JPQL escrito aquí
    List<GastoJpaEntity> findByUsuarioId(Long usuarioId);

    // Between es inclusivo en ambos extremos: fechaEmision en [desde, hasta]
    List<GastoJpaEntity> findByUsuarioIdAndFechaEmisionBetween(Long usuarioId, LocalDate desde, LocalDate hasta);


}
