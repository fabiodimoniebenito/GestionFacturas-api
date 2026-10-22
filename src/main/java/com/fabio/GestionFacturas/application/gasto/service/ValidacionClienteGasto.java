package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteNoEncontradoException;

import java.util.Optional;

/**
 * Rule shared by creating and editing a Gasto: the referenced cliente must exist, belong to the
 * user and be active. Same message for all three cases so none is distinguishable from outside
 * (mirrors DesactivarClienteService/RegistrarCobroService's existence-hiding convention).
 */
final class ValidacionClienteGasto {

    private ValidacionClienteGasto() {
    }

    static void validar(ClienteRepositoryPort clienteRepositoryPort, Long clienteId, Long usuarioId) {
        Optional<Cliente> clienteOpt = clienteRepositoryPort.buscarPorId(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new ClienteNoEncontradoException("Cliente no encontrado");
        }

        Cliente cliente = clienteOpt.get();

        if (!cliente.getUsuarioId().equals(usuarioId)) {
            throw new ClienteNoEncontradoException("Cliente no encontrado");
        }

        if (!cliente.isActivo()) {
            throw new ClienteNoEncontradoException("Cliente no encontrado");
        }
    }
}
