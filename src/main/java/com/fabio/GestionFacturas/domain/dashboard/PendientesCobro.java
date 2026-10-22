package com.fabio.GestionFacturas.domain.dashboard;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;

import java.util.List;

/**
 * Ingresos pendientes de cobro, con el importe adeudado.
 *
 * @param ingresos los ingresos PENDIENTE, en el orden recibido
 * @param total    suma de su total (con IVA); nunca null, un importe ausente es cero
 */
public record PendientesCobro(List<Ingreso> ingresos, Dinero total) {
}
