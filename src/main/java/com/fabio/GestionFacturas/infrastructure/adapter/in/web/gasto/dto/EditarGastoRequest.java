package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request DTO for editing an expense — replaces the editable fields; estado is never part of it. */
public record EditarGastoRequest(
        String emisor,
        LocalDate fechaEmision,
        @PositiveOrZero BigDecimal baseImponible,
        @PositiveOrZero BigDecimal iva,
        @NotNull @PositiveOrZero BigDecimal total,
        String moneda,
        Long clienteId,
        Long categoriaId,
        boolean deducible
) {}
