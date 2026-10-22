package com.fabio.GestionFacturas.application.cliente.port.in;

import com.fabio.GestionFacturas.domain.cliente.Cliente;

import java.util.List;
import java.util.Optional;

/** Puerto de entrada para consultar clientes. Lo implementa {@code ConsultarClientesService}. */
public interface ConsultarClientesUseCase {

    List<Cliente> listarPorUsuario(Long usuarioId);

    /** Devuelve Optional en lugar de lanzar excepción: un cliente de otro usuario se trata como "no encontrado". */
    Optional<Cliente> obtenerPorId(Long clienteId, Long usuarioId);
}
