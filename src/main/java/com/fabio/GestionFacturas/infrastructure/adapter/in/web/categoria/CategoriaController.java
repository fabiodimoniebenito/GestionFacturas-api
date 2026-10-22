package com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria;

import com.fabio.GestionFacturas.application.categoria.port.in.ConsultarCategoriaUseCase;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria.dto.CategoriaResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adapter web de categorías. Ejemplo mínimo de la cadena completa:
 * controller -> ConsultarCategoriaUseCase (puerto) -> ConsultarCategoriaService -> CategoriaRepositoryPort
 * (puerto) -> CategoriaPersistenceAdapter (JPA) -> Categoria de dominio -> CategoriaWebMapper -> DTO de respuesta.
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final ConsultarCategoriaUseCase consultarCategoriaUseCase;

    public CategoriaController(ConsultarCategoriaUseCase consultarCategoriaUseCase) {
        this.consultarCategoriaUseCase = consultarCategoriaUseCase;
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return consultarCategoriaUseCase.listarTodas()
                .stream()
                .map(CategoriaWebMapper::aRespuesta)
                .toList();
    }
}
