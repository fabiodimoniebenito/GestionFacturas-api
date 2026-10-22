package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso;

import com.fabio.GestionFacturas.domain.ingreso.EstadoCobro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/** Repositorio Spring Data; el CRUD viene de {@code JpaRepository}. Solo lo usa {@code IngresoPersistenceAdapter}. */
public interface IngresoJpaRepository extends JpaRepository<IngresoJpaEntity, Long> {

    // Spring Data deriva la consulta del nombre del método: no hay SQL/JPQL escrito aquí
    List<IngresoJpaEntity> findByUsuarioId(Long usuarioId);

    // Between es inclusivo en ambos extremos: fechaEmision en [desde, hasta]
    List<IngresoJpaEntity> findByUsuarioIdAndFechaEmisionBetween(Long usuarioId, LocalDate desde, LocalDate hasta);

    // Del más antiguo al más reciente: las facturas que llevan más tiempo sin cobrar van primero
    List<IngresoJpaEntity> findByUsuarioIdAndEstadoCobroOrderByFechaEmisionAsc(Long usuarioId, EstadoCobro estadoCobro);

}
