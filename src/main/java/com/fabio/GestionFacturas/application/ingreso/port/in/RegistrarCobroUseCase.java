package com.fabio.GestionFacturas.application.ingreso.port.in;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.time.LocalDate;

/**
 * Puerto de entrada para registrar el cobro de una factura.
 * Lo implementa {@code RegistrarCobroService}.
 */
public interface RegistrarCobroUseCase {

    Ingreso registrarCobro(ComandoRegistrarCobro comando);

    record ComandoRegistrarCobro(Long ingresoId, Long usuarioId, LocalDate fechaCobro) {}
}
