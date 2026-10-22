package com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** DTO de respuesta que {@code IngresoWebMapper} construye desde un Ingreso de dominio (la parte de salida del contrato web). */
public record IngresoResponse(
        Long id,
        Long usuarioId,
        Long clienteId,
        String concepto,
        LocalDate fechaEmision,
        BigDecimal baseImponible,
        BigDecimal iva,
        BigDecimal total,
        String moneda,
        String estadoCobro,
        LocalDate fechaCobro,
        LocalDateTime creadoEn
) {}
