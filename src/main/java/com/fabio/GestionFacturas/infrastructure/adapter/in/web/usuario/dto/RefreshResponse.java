package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

/** DTO de respuesta de la renovación: solo un nuevo access token, sin refresh token (aún no hay rotación). */
public record RefreshResponse(
        String accessToken
) {
}
