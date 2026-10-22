package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto;

import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase.ComandoCrearGasto;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.CrearGastoRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.GastoResponse;

/**
 * Convierte entre los DTOs web y el {@link Gasto} de dominio; es la frontera que mantiene separados
 * los tres modelos (DTO / dominio / entidad JPA). Sin estado ni dependencias: solo mapeo.
 */
public class GastoWebMapper {

    public GastoWebMapper() {
    }

    public static ComandoCrearGasto aComando(CrearGastoRequest request, Long usuarioId) {
        return new ComandoCrearGasto(
                usuarioId,
                request.emisor(),
                request.fechaEmision(),
                request.baseImponible(),
                request.iva(),
                request.total(),
                request.moneda(),
                request.clienteId()
        );
    }

    public static GastoResponse aRespuesta(Gasto gasto) {
        String moneda = extraerMoneda(gasto);

        return new GastoResponse(
                gasto.getId(),
                gasto.getUsuarioId(),
                gasto.getCategoriaId(),
                gasto.getClienteId(),
                gasto.getEmisor(),
                gasto.getFechaEmision(),
                extraerCantidad(gasto.getBaseImponible()),
                extraerCantidad(gasto.getIva()),
                moneda,
                gasto.isDeducible(),
                gasto.getEstado().name(),
                gasto.getCreadoEn()
        );
    }

    private static java.math.BigDecimal extraerCantidad(Dinero dinero) {
        if (dinero == null) {
            return null;
        }
        return dinero.cantidad();
    }

    // La respuesta tiene un único campo "moneda" aunque cada Dinero lleve la suya;
    // se usa total -> base -> EUR, porque un BORRADOR recién creado puede no tener ningún importe.
    private static String extraerMoneda(Gasto gasto) {
        Dinero total = gasto.getTotal();
        if (total != null) {
            return total.moneda();
        }
        Dinero base = gasto.getBaseImponible();
        if (base != null) {
            return base.moneda();
        }
        return "EUR";
    }
}
