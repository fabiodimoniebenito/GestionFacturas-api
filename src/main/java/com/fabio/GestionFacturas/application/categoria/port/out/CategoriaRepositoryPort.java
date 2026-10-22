package com.fabio.GestionFacturas.application.categoria.port.out;

import com.fabio.GestionFacturas.domain.categoria.Categoria;

import java.util.List;
import java.util.Optional;

/** Puerto de salida implementado en infraestructura por {@code CategoriaPersistenceAdapter} (JPA). */
public interface CategoriaRepositoryPort {

    List<Categoria> buscarTodas();

    Optional<Categoria> buscarPorId(Long id);

}
