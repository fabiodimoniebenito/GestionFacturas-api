package com.fabio.GestionFacturas.application.ingreso.port.in;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.util.List;
import java.util.Optional;

/** Puerto de entrada para consultar facturas emitidas. Lo implementa {@code ConsultarIngresosService}. */
public interface ConsultarIngresosUseCase {

    List<Ingreso> listarPorUsuario(Long usuarioId);

    /** Devuelve Optional en lugar de lanzar excepción: un ingreso de otro usuario se trata como "no encontrado". */
    Optional<Ingreso> obtenerPorId(Long ingresoId, Long usuarioId);

}
