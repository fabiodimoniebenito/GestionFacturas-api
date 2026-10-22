package com.fabio.GestionFacturas.application.ingreso.port.in;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

/**
 * Puerto de entrada para revertir un cobro registrado previamente.
 * Lo implementa {@code RevertirCobroService}.
 */
public interface RevertirCobroUseCase {

    Ingreso revertirCobro(ComandoRevertirCobro comando);

    record ComandoRevertirCobro(Long ingresoId, Long usuarioId) {}
}
