package com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** DTO de petición para registrar un cliente: es el contrato web, nunca el Cliente de dominio. */
public record CrearClienteRequest(
        @NotBlank String nombre,
        @NotBlank String nif,
        @Email String email,
        String telefono
) {}
