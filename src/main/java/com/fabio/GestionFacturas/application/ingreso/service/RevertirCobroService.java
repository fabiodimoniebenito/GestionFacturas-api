package com.fabio.GestionFacturas.application.ingreso.service;

import com.fabio.GestionFacturas.application.ingreso.port.in.RevertirCobroUseCase;
import com.fabio.GestionFacturas.application.ingreso.port.out.IngresoRepositoryPort;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.ingreso.IngresoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/** Implementa {@link RevertirCobroUseCase}. */
@Service
public class RevertirCobroService implements RevertirCobroUseCase {

    private final IngresoRepositoryPort ingresoRepositoryPort;

    public RevertirCobroService(IngresoRepositoryPort ingresoRepositoryPort) {
        this.ingresoRepositoryPort = ingresoRepositoryPort;
    }

    @Override
    @Transactional
    public Ingreso revertirCobro(ComandoRevertirCobro comando) {
        Optional<Ingreso> ingresoOpt = ingresoRepositoryPort.buscarPorId(comando.ingresoId());
        if (ingresoOpt.isEmpty()) {
            throw new IngresoNoEncontradoException("El ingreso no existe");
        }

        Ingreso ingreso = ingresoOpt.get();

        // Control de acceso: no revelar que el ingreso existe si es de otro usuario
        if (!ingreso.getUsuarioId().equals(comando.usuarioId())) {
            throw new IngresoNoEncontradoException("El ingreso no existe");
        }

        ingreso.revertirCobro();
        return ingresoRepositoryPort.guardar(ingreso);
    }
}
