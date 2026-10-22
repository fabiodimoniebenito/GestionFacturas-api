package com.fabio.GestionFacturas.application.categoria.port.in;

import com.fabio.GestionFacturas.domain.categoria.Categoria;

import java.util.List;

/** Puerto de entrada usado por {@code CategoriaController}; lo implementa {@code ConsultarCategoriaService}. */
public interface ConsultarCategoriaUseCase {

    List<Categoria> listarTodas();

}
