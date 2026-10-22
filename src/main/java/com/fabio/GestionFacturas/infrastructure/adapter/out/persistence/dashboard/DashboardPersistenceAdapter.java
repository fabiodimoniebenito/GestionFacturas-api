package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.dashboard;

import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.EstadoCobro;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto.GastoJpaRepository;
import com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto.GastoMapper;
import com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso.IngresoJpaRepository;
import com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso.IngresoMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementa {@link DashboardConsultaPort} reutilizando los repositorios Spring Data de gasto/ingreso
 * y sus mappers. Los resultados se mapean a dominio antes de devolverse, así las entidades JPA no escapan.
 */
@Component
public class DashboardPersistenceAdapter implements DashboardConsultaPort {

    private final GastoJpaRepository gastoJpaRepository;
    private final IngresoJpaRepository ingresoJpaRepository;

    public DashboardPersistenceAdapter(GastoJpaRepository gastoJpaRepository,
                                       IngresoJpaRepository ingresoJpaRepository) {
        this.gastoJpaRepository = gastoJpaRepository;
        this.ingresoJpaRepository = ingresoJpaRepository;
    }

    @Override
    public List<Gasto> buscarGastosPorPeriodo(Long usuarioId, LocalDate desde, LocalDate hasta) {
        return gastoJpaRepository.findByUsuarioIdAndFechaEmisionBetween(usuarioId, desde, hasta)
                .stream()
                .map(GastoMapper::aDominio)
                .toList();
    }

    @Override
    public List<Ingreso> buscarIngresosPorPeriodo(Long usuarioId, LocalDate desde, LocalDate hasta) {
        return ingresoJpaRepository.findByUsuarioIdAndFechaEmisionBetween(usuarioId, desde, hasta)
                .stream()
                .map(IngresoMapper::aDominio)
                .toList();
    }

    @Override
    public List<Ingreso> buscarIngresosPendientesDeCobro(Long usuarioId) {
        return ingresoJpaRepository.findByUsuarioIdAndEstadoCobroOrderByFechaEmisionAsc(usuarioId, EstadoCobro.PENDIENTE)
                .stream()
                .map(IngresoMapper::aDominio)
                .toList();
    }
}
