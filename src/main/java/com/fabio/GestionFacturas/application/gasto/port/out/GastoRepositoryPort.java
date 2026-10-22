package com.fabio.GestionFacturas.application.gasto.port.out;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Gasto. Está en la capa de aplicación para que los services
 * dependan solo de este contrato; {@code GastoPersistenceAdapter} (infraestructura) lo implementa con JPA.
 */
public interface GastoRepositoryPort {

    Gasto guardar(Gasto gasto);

    Optional <Gasto> buscarPorId(Long id);

    List<Gasto> buscarPorUsuario(Long usuarioId);

}
