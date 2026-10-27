package com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard;

import com.fabio.GestionFacturas.domain.dashboard.PendientesCobro;
import com.fabio.GestionFacturas.domain.dashboard.RentabilidadCliente;
import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto.IngresoPendienteResponse;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto.PendientesCobroResponse;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto.RentabilidadClienteResponse;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.dashboard.dto.ResumenPeriodoResponse;

import java.math.BigDecimal;
import java.util.List;

/** Convierte los resultados de dominio del dashboard en DTOs web, para que los objetos de dominio no crucen la frontera HTTP. */
public class DashboardWebMapper {

    private DashboardWebMapper() {
    }

    public static ResumenPeriodoResponse aRespuesta(ResumenPeriodo resumen) {
        return new ResumenPeriodoResponse(
                resumen.facturadoTotal().cantidad(),
                resumen.facturadoBase().cantidad(),
                resumen.cobradoTotal().cantidad(),
                resumen.cobradoBase().cantidad(),
                resumen.gastosTotal().cantidad(),
                resumen.gastosBase().cantidad(),
                resumen.beneficioCajaTotal().cantidad(),
                resumen.beneficioCajaBase().cantidad(),
                resumen.beneficioFacturadoTotal().cantidad(),
                resumen.beneficioFacturadoBase().cantidad());
    }

    public static PendientesCobroResponse aRespuesta(PendientesCobro pendientes) {
        List<IngresoPendienteResponse> items = pendientes.ingresos()
                .stream()
                .map(DashboardWebMapper::aItem)
                .toList();
        return new PendientesCobroResponse(items, pendientes.total().cantidad());
    }

    public static List<RentabilidadClienteResponse> aRespuesta(List<RentabilidadCliente> rentabilidades) {
        return rentabilidades.stream()
                .map(r -> new RentabilidadClienteResponse(r.clienteId(), r.clienteNombre(), aRespuesta(r.resumen())))
                .toList();
    }

    private static IngresoPendienteResponse aItem(Ingreso ingreso) {
        return new IngresoPendienteResponse(
                ingreso.getId(),
                ingreso.getClienteId(),
                ingreso.getConcepto(),
                ingreso.getFechaEmision(),
                cantidadONull(ingreso.getBaseImponible()),
                cantidadONull(ingreso.getIva()),
                cantidadONull(ingreso.getTotal()));
    }

    /** Los importes del ingreso pueden ser null en el dominio; en la respuesta se mantienen null. */
    private static BigDecimal cantidadONull(Dinero dinero) {
        if (dinero == null) {
            return null;
        }
        return dinero.cantidad();
    }
}
