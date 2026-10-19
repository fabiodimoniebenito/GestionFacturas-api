package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Inbound port for editing the user-editable fields of an existing expense (estado is not changed). */
public interface EditarGastoUseCase {

    Gasto editar(ComandoEditarGasto comando);

    record ComandoEditarGasto(
            Long gastoId,
            Long usuarioId,
            String emisor,
            LocalDate fechaEmision,
            BigDecimal baseImponible,
            BigDecimal iva,
            BigDecimal total,
            String moneda,
            Long clienteId,
            Long categoriaId,
            boolean deducible
    ){}
}
