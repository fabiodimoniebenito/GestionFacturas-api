package com.fabio.GestionFacturas.domain.dashboard;

/**
 * Rentabilidad de un cliente en un periodo: las mismas cifras que {@link ResumenPeriodo},
 * limitadas a los ingresos y gastos vinculados a ese cliente.
 *
 * @param clienteId     el cliente, o {@code null} para la entrada "sin cliente" (gastos sin cliente)
 * @param clienteNombre nombre del cliente; null en la entrada "sin cliente" y mientras nadie lo resuelva
 *                      (la calculadora no conoce nombres: los resuelve el service)
 * @param resumen       las cifras del cliente; nunca null
 */
public record RentabilidadCliente(Long clienteId, String clienteNombre, ResumenPeriodo resumen) {

    /** Sin nombre resuelto: así la crea la calculadora de dominio. */
    public RentabilidadCliente(Long clienteId, ResumenPeriodo resumen) {
        this(clienteId, null, resumen);
    }

    public RentabilidadCliente conNombre(String nombre) {
        return new RentabilidadCliente(clienteId, nombre, resumen);
    }

    public boolean esSinCliente() {
        return clienteId == null;
    }
}
