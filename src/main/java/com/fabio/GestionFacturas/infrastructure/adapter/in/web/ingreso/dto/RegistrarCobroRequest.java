package com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto;

import java.time.LocalDate;

/** DTO de petición para registrar el cobro de una factura. */
public record RegistrarCobroRequest(LocalDate fechaCobro) {}
