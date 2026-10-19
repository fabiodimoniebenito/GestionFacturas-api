package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Response DTO built by {@code GastoWebMapper} from a domain Gasto — the outbound half of the web contract. */
public record GastoResponse(
        Long id,
        Long usuarioId,
        Long categoriaId,
        Long clienteId,
        String emisor,
        LocalDate fechaEmision,
        BigDecimal baseImponible,
        BigDecimal iva,
        String moneda,
        boolean deducible,
        @Schema(allowableValues = {"BORRADOR", "PROCESADA", "REVISADA", "EXPORTADA"})
        String estado,
        LocalDateTime creadoEn
) {}
