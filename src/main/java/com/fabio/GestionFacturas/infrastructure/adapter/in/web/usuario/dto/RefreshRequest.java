package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO de petición del endpoint de renovación de token. */
public record RefreshRequest(
        @NotBlank String refreshToken
) {
}
