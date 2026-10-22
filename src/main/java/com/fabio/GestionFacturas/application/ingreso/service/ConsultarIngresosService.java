package com.fabio.GestionFacturas.application.ingreso.service;

import com.fabio.GestionFacturas.application.ingreso.port.in.ConsultarIngresosUseCase;
import com.fabio.GestionFacturas.application.ingreso.port.out.IngresoRepositoryPort;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Implementa {@link ConsultarIngresosUseCase} (lectura) sobre {@link IngresoRepositoryPort}. */
@Service
public class ConsultarIngresosService implements ConsultarIngresosUseCase {

    private final IngresoRepositoryPort ingresoRepository;

    public ConsultarIngresosService(IngresoRepositoryPort ingresoRepository) {
        this.ingresoRepository = ingresoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingreso> listarPorUsuario(Long usuarioId) {
        return ingresoRepository.buscarPorUsuario(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ingreso> obtenerPorId(Long ingresoId, Long usuarioId) {
        Optional<Ingreso> ingreso = ingresoRepository.buscarPorId(ingresoId);
        if (ingreso.isEmpty()) {
            return Optional.empty();
        }
        // Control de acceso: un ingreso de otro usuario se devuelve como inexistente, no como prohibido
        if (!ingreso.get().getUsuarioId().equals(usuarioId)) {
            return Optional.empty();
        }
        return ingreso;
    }
}
