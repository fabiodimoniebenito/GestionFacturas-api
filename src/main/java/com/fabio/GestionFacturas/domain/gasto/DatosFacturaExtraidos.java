package com.fabio.GestionFacturas.domain.gasto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resultado de {@link FacturaTextParser}: campos extraídos del texto bruto del OCR.
 * Cualquier campo puede ser null si el parser no lo encontró; el usuario lo completa al revisar.
 */
public record DatosFacturaExtraidos(
        String emisor,
        LocalDate fechaEmision,
        BigDecimal baseImponible,
        BigDecimal iva,
        BigDecimal total
) {
}
