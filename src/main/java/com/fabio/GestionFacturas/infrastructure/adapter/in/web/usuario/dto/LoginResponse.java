package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

/** DTO de respuesta del login: único sitio donde ambos tokens viajan en claro; el cliente debe guardarlos de forma segura. */
public record LoginResponse(

        String accessToken,
        String refreshToken,
        Long usuarioId,
        String email

) {
}
