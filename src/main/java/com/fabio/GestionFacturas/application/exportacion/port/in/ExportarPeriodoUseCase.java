package com.fabio.GestionFacturas.application.exportacion.port.in;

import java.time.LocalDate;

/** Puerto de entrada para exportar un periodo a Excel. Lo implementa {@code ExportarPeriodoService}. */
public interface ExportarPeriodoUseCase {

    byte[] exportar(ComandoExportarPeriodo comando);

    record ComandoExportarPeriodo(Long usuarioId, LocalDate desde, LocalDate hasta) {}
}
