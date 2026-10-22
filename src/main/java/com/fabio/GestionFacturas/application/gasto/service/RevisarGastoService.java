package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.gasto.port.in.RevisarGastoUseCase;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.gasto.GastoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Implements {@link RevisarGastoUseCase}; the transition rules live in {@link Gasto#revisar()}. */
@Service
public class RevisarGastoService implements RevisarGastoUseCase {

    private final GastoRepositoryPort gastoRepositoryPort;

    public RevisarGastoService(GastoRepositoryPort gastoRepositoryPort) {
        this.gastoRepositoryPort = gastoRepositoryPort;
    }

    @Override
    @Transactional
    public Gasto revisar(Long gastoId, Long usuarioId) {
        Optional<Gasto> gastoOpt = gastoRepositoryPort.buscarPorId(gastoId);
        if (gastoOpt.isEmpty()) {
            throw new GastoNoEncontradoException("Gasto no encontrado");
        }

        Gasto gasto = gastoOpt.get();

        // Access control: another user's gasto is reported exactly like a missing one
        if (!gasto.getUsuarioId().equals(usuarioId)) {
            throw new GastoNoEncontradoException("Gasto no encontrado");
        }

        gasto.revisar();

        return gastoRepositoryPort.guardar(gasto);
    }
}
