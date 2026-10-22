package com.fabio.GestionFacturas.application.cliente.port.in;

import com.fabio.GestionFacturas.domain.cliente.Cliente;

/**
 * Puerto de entrada para registrar un cliente. {@code ClienteController} depende solo de esta
 * interfaz; Spring inyecta la implementación ({@code CrearClienteService}) en tiempo de ejecución.
 */
public interface CrearClienteUseCase {

    Cliente crear(ComandoCrearCliente comando);

    /** Comando: la única forma en que la capa web entrega datos a la capa de aplicación. */
    record ComandoCrearCliente(
            Long usuarioId,
            String nombre,
            String nif,
            String email,
            String telefono
    ) {}
}
