package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

/** DTO de respuesta del usuario registrado; excluye a propósito el hash de la contraseña. */
public record UsuarioResponse(

        Long id,
        String email,
        String nombre
) {
}
