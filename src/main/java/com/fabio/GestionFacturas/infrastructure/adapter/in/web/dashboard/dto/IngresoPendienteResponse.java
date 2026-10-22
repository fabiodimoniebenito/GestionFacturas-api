package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Elemento de GET /api/dashboard/pendientes-cobro: un ingreso aún pendiente de cobro. */
public record IngresoPendienteResponse(
        Long id,
        Long clienteId,
        String concepto,
        LocalDate fechaEmision,
        BigDecimal baseImponible,
        BigDecimal iva,
        BigDecimal total) {
}
