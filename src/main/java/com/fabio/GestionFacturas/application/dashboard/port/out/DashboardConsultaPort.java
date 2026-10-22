package com.fabio.GestionFacturas.application.dashboard.port.out;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de salida para las consultas del dashboard. Solo datos del propio usuario, filtrados por
 * fechaEmision en [desde, hasta] (ambos inclusive). Lo implementa {@code DashboardPersistenceAdapter}.
 */
public interface DashboardConsultaPort {

    List<Gasto> buscarGastosPorPeriodo(Long usuarioId, LocalDate desde, LocalDate hasta);

    List<Ingreso> buscarIngresosPorPeriodo(Long usuarioId, LocalDate desde, LocalDate hasta);

    /** Ingresos del usuario con estadoCobro PENDIENTE, del más antiguo al más reciente por fechaEmision. */
    List<Ingreso> buscarIngresosPendientesDeCobro(Long usuarioId);
}
