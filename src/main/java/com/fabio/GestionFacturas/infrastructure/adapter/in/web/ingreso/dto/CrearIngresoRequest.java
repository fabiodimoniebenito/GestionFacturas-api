package com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/** DTO de petición para emitir una factura: es el contrato web, nunca el Ingreso de dominio. */
public record CrearIngresoRequest(
        Long clienteId,
        String concepto,
        LocalDate fechaEmision,
        @PositiveOrZero BigDecimal baseImponible,
        @PositiveOrZero BigDecimal iva,
        @NotNull @PositiveOrZero BigDecimal total,
        String moneda
) {}
