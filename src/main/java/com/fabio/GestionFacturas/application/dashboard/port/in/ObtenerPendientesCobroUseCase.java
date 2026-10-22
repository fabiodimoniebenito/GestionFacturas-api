package com.fabio.GestionFacturas.application.dashboard.port.in;

import com.fabio.GestionFacturas.domain.dashboard.PendientesCobro;

/** Puerto de entrada para la lista de pendientes de cobro del dashboard. Lo implementa {@code PendientesCobroService}. */
public interface ObtenerPendientesCobroUseCase {

    PendientesCobro obtenerPendientes(ComandoObtenerPendientesCobro comando);

    record ComandoObtenerPendientesCobro(Long usuarioId) {}
}
