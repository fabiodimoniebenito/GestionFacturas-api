package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto;

import java.math.BigDecimal;

/** Respuesta de GET /api/dashboard/resumen: cifras del resumen del periodo (total con IVA, base sin IVA). */
public record ResumenPeriodoResponse(
        BigDecimal facturadoTotal,
        BigDecimal facturadoBase,
        BigDecimal cobradoTotal,
        BigDecimal cobradoBase,
        BigDecimal gastosTotal,
        BigDecimal gastosBase,
        BigDecimal beneficioCajaTotal,
        BigDecimal beneficioCajaBase,
        BigDecimal beneficioFacturadoTotal,
        BigDecimal beneficioFacturadoBase) {
}
