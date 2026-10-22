package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso;

import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;

import java.math.BigDecimal;

/**
 * Convierte entre el {@link Ingreso} de dominio y {@link IngresoJpaEntity}: el tercero de los tres
 * modelos (DTO / dominio / entidad JPA). Solo lo usa {@code IngresoPersistenceAdapter}.
 */
public class IngresoMapper {

    public IngresoMapper() {
    }

    public static IngresoJpaEntity aEntidad(Ingreso ingreso) {
        String moneda = extraerMoneda(ingreso);

        return new IngresoJpaEntity(
                ingreso.getId(),
                ingreso.getUsuarioId(),
                ingreso.getClienteId(),
                ingreso.getConcepto(),
                ingreso.getFechaEmision(),
                extraerCantidad(ingreso.getBaseImponible()),
                extraerCantidad(ingreso.getIva()),
                extraerCantidad(ingreso.getTotal()),
                moneda,
                ingreso.getEstadoCobro(),
                ingreso.getFechaCobro(),
                ingreso.getCreadoEn()
        );
    }

    public static Ingreso aDominio(IngresoJpaEntity entidad) {
        String moneda = entidad.getMoneda();

        return new Ingreso(
                entidad.getId(),
                entidad.getUsuarioId(),
                entidad.getClienteId(),
                entidad.getConcepto(),
                entidad.getFechaEmision(),
                construirDinero(entidad.getBaseImponible(), moneda),
                construirDinero(entidad.getIva(), moneda),
                construirDinero(entidad.getTotal(), moneda),
                entidad.getEstadoCobro(),
                entidad.getFechaCobro(),
                entidad.getCreadoEn()
        );
    }

    private static BigDecimal extraerCantidad(Dinero dinero) {
        if (dinero == null) {
            return null;
        }
        return dinero.cantidad();
    }

    // Mismo fallback que el mapper web: el dominio tiene un Dinero por importe y la entidad una sola columna moneda
    private static String extraerMoneda(Ingreso ingreso) {
        Dinero total = ingreso.getTotal();
        if (total != null) {
            return total.moneda();
        }
        Dinero base = ingreso.getBaseImponible();
        if (base != null) {
            return base.moneda();
        }
        return "EUR";
    }

    private static Dinero construirDinero(BigDecimal cantidad, String moneda) {
        if (cantidad == null) {
            return null;
        }
        return new Dinero(cantidad, moneda);
    }
}
