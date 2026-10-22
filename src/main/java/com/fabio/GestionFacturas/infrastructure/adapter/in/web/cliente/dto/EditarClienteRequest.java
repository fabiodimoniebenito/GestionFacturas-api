package com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** DTO de petición para editar los campos modificables de un cliente. No incluye nif: no se puede cambiar tras la creación. */
public record EditarClienteRequest(
        @NotBlank String nombre,
        @Email String email,
        String telefono
) {}
