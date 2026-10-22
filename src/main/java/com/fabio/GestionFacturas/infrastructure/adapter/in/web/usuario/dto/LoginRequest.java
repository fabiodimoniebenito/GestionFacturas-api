package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO de petición de login. */
public record LoginRequest(

        @NotBlank String email,
        @NotBlank String password
) {
}
