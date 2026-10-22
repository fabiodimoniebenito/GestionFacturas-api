package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO de petición de logout. */
public record LogoutRequest(
        @NotBlank String refreshToken
) {
}
