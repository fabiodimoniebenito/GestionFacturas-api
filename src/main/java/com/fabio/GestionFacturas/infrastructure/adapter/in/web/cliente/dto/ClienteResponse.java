package com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto;

/** DTO de respuesta que {@code ClienteWebMapper} construye desde un Cliente de dominio (la parte de salida del contrato web). */
public record ClienteResponse(
        Long id,
        Long usuarioId,
        String nombre,
        String nif,
        String email,
        String telefono,
        boolean activo
) {}
