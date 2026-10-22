package com.fabio.GestionFacturas.application.cliente.service;

import com.fabio.GestionFacturas.application.cliente.port.in.EditarClienteUseCase;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteInvalidoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementa {@link EditarClienteUseCase}. Un cliente inexistente y uno de otro usuario se
 * reportan igual con {@link ClienteInvalidoException} (400): al editar no hay un retorno "vacío" natural,
 * así que se reutiliza una excepción ya mapeada.
 */
@Service
public class EditarClienteService implements EditarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public EditarClienteService(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    @Transactional
    public Cliente editar(ComandoEditarCliente comando) {
        Optional<Cliente> clienteOpt = clienteRepositoryPort.buscarPorId(comando.clienteId());
        if (clienteOpt.isEmpty()) {
            throw new ClienteInvalidoException("El cliente no existe");
        }

        Cliente cliente = clienteOpt.get();

        // Control de acceso: no revelar que el cliente existe si es de otro usuario
        if (!cliente.getUsuarioId().equals(comando.usuarioId())) {
            throw new ClienteInvalidoException("El cliente no existe");
        }

        cliente.actualizarDatos(comando.nombre(), comando.email(), comando.telefono());

        return clienteRepositoryPort.guardar(cliente);
    }
}
