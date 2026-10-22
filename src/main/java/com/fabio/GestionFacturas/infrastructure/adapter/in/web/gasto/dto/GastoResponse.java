package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** DTO de respuesta que {@code GastoWebMapper} construye desde un Gasto de dominio (la parte de salida del contrato web). */
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
