package com.fabio.GestionFacturas.domain.dashboard;

import com.fabio.GestionFacturas.domain.shared.Dinero;

/**
 * Resumen de periodo para el dashboard. Cada cifra se da en total (con IVA) y en base (sin IVA).
 * Nunca es null: un importe ausente es cero.
 *
 * @param facturadoTotal          suma del total de TODOS los ingresos (sea cual sea su estadoCobro)
 * @param facturadoBase           suma de la baseImponible de TODOS los ingresos
 * @param cobradoTotal            suma del total de los ingresos COBRADA únicamente
 * @param cobradoBase             suma de la baseImponible de los ingresos COBRADA únicamente
 * @param gastosTotal             suma del total de todos los gastos
 * @param gastosBase              suma de la baseImponible de todos los gastos
 * @param beneficioCajaTotal      cobradoTotal - gastosTotal
 * @param beneficioCajaBase       cobradoBase - gastosBase
 * @param beneficioFacturadoTotal facturadoTotal - gastosTotal
 * @param beneficioFacturadoBase  facturadoBase - gastosBase
 */
public record ResumenPeriodo(
        Dinero facturadoTotal,
        Dinero facturadoBase,
        Dinero cobradoTotal,
        Dinero cobradoBase,
        Dinero gastosTotal,
        Dinero gastosBase,
        Dinero beneficioCajaTotal,
        Dinero beneficioCajaBase,
        Dinero beneficioFacturadoTotal,
        Dinero beneficioFacturadoBase) {
}
