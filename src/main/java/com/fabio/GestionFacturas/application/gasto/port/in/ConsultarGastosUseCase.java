package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

import java.util.List;
import java.util.Optional;

/** Puerto de entrada para consultar gastos. Lo implementa {@code ConsultarGastosService}. */
public interface ConsultarGastosUseCase {

    List<Gasto> listarPorUsuario(Long usuarioId);

    /** Devuelve Optional en lugar de lanzar excepción: un gasto de otro usuario se trata como "no encontrado". */
    Optional<Gasto> obtenerPorId(Long gastoId, Long usuarioId);

}
