package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Puerto de entrada para registrar un gasto manualmente (sin OCR).
 * {@code GastoController} depende solo de esta interfaz; Spring inyecta {@code CrearGastoService}
 * en tiempo de ejecución, así la capa web no sabe cómo se crea realmente el gasto.
 */
public interface CrearGastoUseCase {

    Gasto crear(ComandoCrearGasto comando);

    /** Comando: la única forma en que la capa web entrega datos a la capa de aplicación. */
    record ComandoCrearGasto(
            Long usuarioId,
            String emisor,
            LocalDate fechaEmision,
            BigDecimal baseImponible,
            BigDecimal iva,
            BigDecimal total,
            String moneda,
            Long clienteId

    ){}


    }

