package com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria;

import com.fabio.GestionFacturas.domain.categoria.Categoria;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria.dto.CategoriaResponse;

/** Convierte la {@link Categoria} de dominio en su DTO de respuesta, para que el objeto de dominio no salga por HTTP. */
public class CategoriaWebMapper {

    public CategoriaWebMapper() {
    }

    public static CategoriaResponse aRespuesta(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.isDeduciblePorDefecto()
        );
    }
}
