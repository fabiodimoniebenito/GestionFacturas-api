package com.fabio.GestionFacturas.application.ingreso.port.in;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Puerto de entrada para registrar una nueva factura emitida a un cliente. */
public interface CrearIngresoUseCase {

    Ingreso crear(ComandoCrearIngreso comando);

    /** Comando: la única forma en que la capa web entrega datos a la capa de aplicación. */
    record ComandoCrearIngreso(
            Long usuarioId,
            Long clienteId,
            String concepto,
            LocalDate fechaEmision,
            BigDecimal baseImponible,
            BigDecimal iva,
            BigDecimal total,
            String moneda
    ) {}
}
