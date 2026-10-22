package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

/** Inbound port for marking an expense as reviewed (REVISADA). */
public interface RevisarGastoUseCase {

    Gasto revisar(Long gastoId, Long usuarioId);
}
