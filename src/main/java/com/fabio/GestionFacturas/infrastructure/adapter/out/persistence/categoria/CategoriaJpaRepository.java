package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.categoria;

import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio Spring Data; con el CRUD estándar basta, sin consultas propias. */
public interface CategoriaJpaRepository extends JpaRepository<CategoriaJpaEntity, Long> {
}
