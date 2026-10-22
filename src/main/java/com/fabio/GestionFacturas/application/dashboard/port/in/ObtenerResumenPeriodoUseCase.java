package com.fabio.GestionFacturas.application.dashboard.port.in;

import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;

import java.time.LocalDate;

/** Puerto de entrada para el resumen de periodo del dashboard. Lo implementa {@code ResumenPeriodoService}. */
public interface ObtenerResumenPeriodoUseCase {

    ResumenPeriodo obtenerResumen(ComandoObtenerResumen comando);

    record ComandoObtenerResumen(Long usuarioId, LocalDate desde, LocalDate hasta) {}
}
