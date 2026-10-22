package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

/** Respuesta de GET /api/dashboard/pendientes-cobro: ingresos pendientes (los más antiguos primero) y total adeudado (con IVA). */
public record PendientesCobroResponse(
        List<IngresoPendienteResponse> ingresos,
        BigDecimal total) {
}
