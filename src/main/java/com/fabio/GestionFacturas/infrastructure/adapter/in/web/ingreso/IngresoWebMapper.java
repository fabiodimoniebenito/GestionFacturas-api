package com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso;

import com.fabio.GestionFacturas.application.ingreso.port.in.CrearIngresoUseCase.ComandoCrearIngreso;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto.CrearIngresoRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto.IngresoResponse;

import java.math.BigDecimal;

/**
 * Convierte entre los DTOs web y el {@link Ingreso} de dominio; es la frontera que mantiene separados
 * los tres modelos (DTO / dominio / entidad JPA). Sin estado ni dependencias: solo mapeo.
 */
public class IngresoWebMapper {

    public IngresoWebMapper() {
    }

    public static ComandoCrearIngreso aComando(CrearIngresoRequest request, Long usuarioId) {
        return new ComandoCrearIngreso(
                usuarioId,
                request.clienteId(),
                request.concepto(),
                request.fechaEmision(),
                request.baseImponible(),
                request.iva(),
                request.total(),
                request.moneda()
        );
    }

    public static IngresoResponse aRespuesta(Ingreso ingreso) {
        String moneda = extraerMoneda(ingreso);

        return new IngresoResponse(
                ingreso.getId(),
                ingreso.getUsuarioId(),
                ingreso.getClienteId(),
                ingreso.getConcepto(),
                ingreso.getFechaEmision(),
                extraerCantidad(ingreso.getBaseImponible()),
                extraerCantidad(ingreso.getIva()),
                extraerCantidad(ingreso.getTotal()),
                moneda,
                ingreso.getEstadoCobro().name(),
                ingreso.getFechaCobro(),
                ingreso.getCreadoEn()
        );
    }

    private static BigDecimal extraerCantidad(Dinero dinero) {
        if (dinero == null) {
            return null;
        }
        return dinero.cantidad();
    }

    // La respuesta tiene un único campo "moneda" aunque cada Dinero lleve la suya;
    // se usa total -> base -> EUR, porque un ingreso recién creado puede no tener ningún importe.
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
}
