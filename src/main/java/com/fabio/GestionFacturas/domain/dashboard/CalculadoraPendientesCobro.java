package com.fabio.GestionFacturas.domain.dashboard;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import java.math.BigDecimal;
import java.util.List;

/**
 * Calculadora de dominio puro: construye PendientesCobro a partir de ingresos que quien llama
 * ya ha filtrado a PENDIENTE. Sin Spring/JPA a propósito (arquitectura hexagonal).
 */
public class CalculadoraPendientesCobro {

    public PendientesCobro calcular(List<Ingreso> pendientes) {
        BigDecimal total = BigDecimal.ZERO;

        for (Ingreso ingreso : pendientes) {
            total = total.add(cantidadOCero(ingreso.getTotal()));
        }

        return new PendientesCobro(List.copyOf(pendientes), Dinero.deEuros(total));
    }

    /** Los importes pueden ser null en el dominio; cuentan como cero en la suma. */
    private BigDecimal cantidadOCero(Dinero dinero) {
        if (dinero == null) {
            return BigDecimal.ZERO;
        }
        return dinero.cantidad();
    }
}
