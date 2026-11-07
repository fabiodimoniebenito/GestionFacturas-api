package com.fabio.GestionFacturas.application.shared.port.out;

import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;
import com.fabio.GestionFacturas.domain.gasto.EstadoGasto;
import com.fabio.GestionFacturas.domain.ingreso.EstadoCobro;
import com.fabio.GestionFacturas.domain.shared.Dinero;

import java.time.LocalDate;
import java.util.List;

/**
 * Datos ya calculados y con los nombres resueltos que el adapter vuelca en el fichero. No contiene lógica:
 * las cifras provienen de las calculadoras de dominio del dashboard. Los importes y fechas pueden ser null.
 */
public record DatosExportacion(
        LocalDate desde,
        LocalDate hasta,
        ResumenPeriodo resumen,
        List<FilaIngreso> ingresos,
        List<FilaGasto> gastos,
        List<FilaPendiente> pendientes,
        Dinero totalPendiente,
        List<FilaRentabilidad> rentabilidad) {

    public record FilaIngreso(LocalDate fechaEmision, String clienteNombre, String concepto,
                              Dinero base, Dinero iva, Dinero total,
                              EstadoCobro estadoCobro, LocalDate fechaCobro) {}

    public record FilaGasto(LocalDate fecha, String emisor, String clienteNombre, String categoriaNombre,
                            Dinero base, Dinero iva, Dinero total, EstadoGasto estado, boolean deducible) {}

    public record FilaPendiente(LocalDate fechaEmision, String clienteNombre, String concepto, Dinero total) {}

    /** La entrada "sin cliente" llega ya con la etiqueta "Gastos generales (sin cliente)". */
    public record FilaRentabilidad(String nombre, ResumenPeriodo resumen) {}
}
