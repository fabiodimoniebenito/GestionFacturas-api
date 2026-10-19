package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.categoria.port.out.CategoriaRepositoryPort;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.application.gasto.port.in.EditarGastoUseCase;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.categoria.CategoriaNoEncontradaException;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.gasto.GastoNoEncontradoException;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Implements {@link EditarGastoUseCase}. Orchestrates only: ownership and reference checks here,
 * every business rule (amounts, EXPORTADA lock) lives in {@link Gasto#actualizar}.
 */
@Service
public class EditarGastoService implements EditarGastoUseCase {

    private final GastoRepositoryPort gastoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public EditarGastoService(GastoRepositoryPort gastoRepositoryPort,
                              ClienteRepositoryPort clienteRepositoryPort,
                              CategoriaRepositoryPort categoriaRepositoryPort) {
        this.gastoRepositoryPort = gastoRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    @Override
    @Transactional
    public Gasto editar(ComandoEditarGasto comando) {
        Optional<Gasto> gastoOpt = gastoRepositoryPort.buscarPorId(comando.gastoId());
        if (gastoOpt.isEmpty()) {
            throw new GastoNoEncontradoException("Gasto no encontrado");
        }

        Gasto gasto = gastoOpt.get();

        // Access control: another user's gasto is reported exactly like a missing one
        if (!gasto.getUsuarioId().equals(comando.usuarioId())) {
            throw new GastoNoEncontradoException("Gasto no encontrado");
        }

        if (comando.categoriaId() != null) {
            if (categoriaRepositoryPort.buscarPorId(comando.categoriaId()).isEmpty()) {
                throw new CategoriaNoEncontradaException("Categoría no encontrada");
            }
        }

        // Only re-validate the cliente when it actually changes; null clears it
        if (comando.clienteId() != null && !comando.clienteId().equals(gasto.getClienteId())) {
            ValidacionClienteGasto.validar(clienteRepositoryPort, comando.clienteId(), comando.usuarioId());
        }

        String moneda = comando.moneda();
        if (moneda == null) {
            moneda = "EUR";
        }

        gasto.actualizar(comando.emisor(), comando.fechaEmision(),
                aDinero(comando.baseImponible(), moneda),
                aDinero(comando.iva(), moneda),
                aDinero(comando.total(), moneda),
                comando.clienteId(), comando.categoriaId(), comando.deducible());

        return gastoRepositoryPort.guardar(gasto);
    }

    private Dinero aDinero(BigDecimal cantidad, String moneda) {
        if (cantidad == null) {
            return null;
        }
        return new Dinero(cantidad, moneda);
    }
}
