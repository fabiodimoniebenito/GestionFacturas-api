package com.fabio.GestionFacturas.application.ingreso.port.out;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Ingreso. Está en la capa de aplicación para que los services
 * dependan solo de este contrato; {@code IngresoPersistenceAdapter} (infraestructura) lo implementa con JPA.
 */
public interface IngresoRepositoryPort {

    Ingreso guardar(Ingreso ingreso);

    Optional<Ingreso> buscarPorId(Long id);

    List<Ingreso> buscarPorUsuario(Long usuarioId);

}
