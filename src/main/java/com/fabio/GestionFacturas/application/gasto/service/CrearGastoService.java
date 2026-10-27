package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fabio.GestionFacturas.domain.cliente.ClienteNoEncontradoException;

import java.util.Optional;

/**
 * Implementa {@link CrearGastoUseCase}. Lo invoca {@code GastoController} y habla solo con
 * {@link GastoRepositoryPort}, nunca con el adapter JPA: así se puede cambiar la persistencia sin tocar esta clase.
 */
@Service
public class CrearGastoService implements CrearGastoUseCase {

    private final GastoRepositoryPort gastoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;

    // Solo inyección por constructor (convención del proyecto): sin @Autowired en campos, más fácil de testear con mocks
    public CrearGastoService(GastoRepositoryPort gastoRepositoryPort, ClienteRepositoryPort clienteRepositoryPort) {
        this.gastoRepositoryPort = gastoRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    @Transactional // wraps the save in a DB transaction; matters more once this method touches >1 aggregate
    public Gasto crear(ComandoCrearGasto comando) {
        if (comando.clienteId() != null) {
            ValidacionClienteGasto.validar(clienteRepositoryPort, comando.clienteId(), comando.usuarioId());
        }

        String moneda = comando.moneda();
        if (moneda == null) {
            moneda = "EUR";
        }

        Dinero baseImponible = null;
        if (comando.baseImponible() != null) {
            baseImponible = new Dinero(comando.baseImponible(), moneda);
        }

        Dinero iva = null;
        if (comando.iva() != null) {
            iva = new Dinero(comando.iva(), moneda);
        }

        Dinero total = null;
        if (comando.total() != null) {
            total = new Dinero(comando.total(), moneda);
        }

        Gasto gasto = Gasto.crearBorrador(comando.usuarioId(), comando.clienteId(), comando.emisor(),
                comando.fechaEmision(), baseImponible, iva, total);

        return gastoRepositoryPort.guardar(gasto);
    }

    // Mismo mensaje para no encontrado / de otro usuario / inactivo, para que no se distingan desde fuera
    // (igual que DesactivarClienteService y RegistrarCobroService).
    private void validarCliente(Long clienteId, Long usuarioId) {
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
