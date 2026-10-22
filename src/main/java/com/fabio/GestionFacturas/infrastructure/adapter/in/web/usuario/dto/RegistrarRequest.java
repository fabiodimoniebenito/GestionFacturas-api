package com.fabio.GestionFacturas.infrastructure.adapter.in.web.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** DTO de petición de registro: la contraseña en claro solo llega hasta el service, que la hashea. */
public record RegistrarRequest(

        @NotBlank @Email String email,
        @NotBlank String nombre,
        @NotBlank String password
) {
}
