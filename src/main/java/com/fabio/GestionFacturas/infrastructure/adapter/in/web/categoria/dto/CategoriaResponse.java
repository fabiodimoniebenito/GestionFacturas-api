package com.fabio.GestionFacturas.infrastructure.adapter.in.web.categoria.dto;

/** DTO de respuesta del listado de categorías. */
public record CategoriaResponse(
        Long id,
        String nombre,
        boolean deduciblePorDefecto
) {}
