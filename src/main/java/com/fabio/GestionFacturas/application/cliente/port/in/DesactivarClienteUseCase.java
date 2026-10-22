package com.fabio.GestionFacturas.application.cliente.port.in;

/**
 * Puerto de entrada para desactivar un cliente (borrado lógico: {@code activo=false}, la fila se conserva).
 * Lo implementa {@code DesactivarClienteService}.
 */
public interface DesactivarClienteUseCase {

    void desactivar(ComandoDesactivarCliente comando);

    record ComandoDesactivarCliente(Long clienteId, Long usuarioId) {}
}
