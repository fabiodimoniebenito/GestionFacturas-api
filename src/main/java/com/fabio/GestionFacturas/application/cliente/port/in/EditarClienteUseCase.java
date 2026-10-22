package com.fabio.GestionFacturas.application.cliente.port.in;

import com.fabio.GestionFacturas.domain.cliente.Cliente;

/**
 * Puerto de entrada para editar los datos modificables de un cliente. Lo implementa
 * {@code EditarClienteService}, que comprueba que el cliente pertenezca al usuario.
 */
public interface EditarClienteUseCase {

    Cliente editar(ComandoEditarCliente comando);

    /** El nif no se incluye: es inmutable una vez creado el cliente. */
    record ComandoEditarCliente(
            Long clienteId,
            Long usuarioId,
            String nombre,
            String email,
            String telefono
    ) {}
}
