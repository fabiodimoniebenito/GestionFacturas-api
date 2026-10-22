package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto;

/**
 * Elemento de GET /api/dashboard/rentabilidad-clientes. {@code clienteId} es null en la
 * entrada "sin cliente" (gastos no vinculados a ningún cliente).
 */
public record RentabilidadClienteResponse(
        Long clienteId,
        ResumenPeriodoResponse resumen) {
}
