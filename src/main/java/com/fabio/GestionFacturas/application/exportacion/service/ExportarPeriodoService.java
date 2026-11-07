package com.fabio.GestionFacturas.application.exportacion.service;

import com.fabio.GestionFacturas.application.categoria.port.out.CategoriaRepositoryPort;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.application.exportacion.port.in.ExportarPeriodoUseCase;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaGasto;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaIngreso;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaPendiente;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaRentabilidad;
import com.fabio.GestionFacturas.application.shared.port.out.ExportPort;
import com.fabio.GestionFacturas.domain.categoria.Categoria;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraPendientesCobro;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraRentabilidadClientes;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraResumenPeriodo;
import com.fabio.GestionFacturas.domain.dashboard.PendientesCobro;
import com.fabio.GestionFacturas.domain.dashboard.RentabilidadCliente;
import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementa {@link ExportarPeriodoUseCase}: reúne los datos del periodo con los puertos existentes, calcula
 * las cifras con las calculadoras del dashboard (mismos números) y delega la generación del fichero en
 * {@link ExportPort}. No contiene lógica de cálculo propia.
 */
@Service
public class ExportarPeriodoService implements ExportarPeriodoUseCase {

    static final String ETIQUETA_SIN_CLIENTE = "Gastos generales (sin cliente)";

    private final DashboardConsultaPort dashboardConsulta;
    private final ClienteRepositoryPort clienteRepository;
    private final CategoriaRepositoryPort categoriaRepository;
    private final CalculadoraResumenPeriodo calculadoraResumen;
    private final CalculadoraRentabilidadClientes calculadoraRentabilidad;
    private final CalculadoraPendientesCobro calculadoraPendientes;
    private final ExportPort exportPort;

    public ExportarPeriodoService(DashboardConsultaPort dashboardConsulta,
                                  ClienteRepositoryPort clienteRepository,
                                  CategoriaRepositoryPort categoriaRepository,
                                  CalculadoraResumenPeriodo calculadoraResumen,
                                  CalculadoraRentabilidadClientes calculadoraRentabilidad,
                                  CalculadoraPendientesCobro calculadoraPendientes,
                                  ExportPort exportPort) {
        this.dashboardConsulta = dashboardConsulta;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.calculadoraResumen = calculadoraResumen;
        this.calculadoraRentabilidad = calculadoraRentabilidad;
        this.calculadoraPendientes = calculadoraPendientes;
        this.exportPort = exportPort;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportar(ComandoExportarPeriodo comando) {
        if (comando.desde().isAfter(comando.hasta())) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
        Long usuarioId = comando.usuarioId();

        List<Gasto> gastos = dashboardConsulta.buscarGastosPorPeriodo(usuarioId, comando.desde(), comando.hasta());
        List<Ingreso> ingresos = dashboardConsulta.buscarIngresosPorPeriodo(usuarioId, comando.desde(), comando.hasta());
        List<Ingreso> pendientesIngresos = dashboardConsulta.buscarIngresosPendientesDeCobro(usuarioId);

        ResumenPeriodo resumen = calculadoraResumen.calcular(gastos, ingresos);
        List<RentabilidadCliente> rentabilidades = calculadoraRentabilidad.calcular(gastos, ingresos);
        PendientesCobro pendientes = calculadoraPendientes.calcular(pendientesIngresos);

        Map<Long, String> clientes = nombresDeClientes(usuarioId, gastos, ingresos, pendientesIngresos, rentabilidades);
        Map<Long, String> categorias = nombresDeCategorias();

        DatosExportacion datos = new DatosExportacion(
                comando.desde(),
                comando.hasta(),
                resumen,
                filasIngresos(ingresos, clientes),
                filasGastos(gastos, clientes, categorias),
                filasPendientes(pendientes.ingresos(), clientes),
                pendientes.total(),
                filasRentabilidad(rentabilidades, clientes));

        return exportPort.generarExcel(datos);
    }

    /** Una única consulta por lote; incluye clientes inactivos y solo los del usuario. */
    private Map<Long, String> nombresDeClientes(Long usuarioId, List<Gasto> gastos, List<Ingreso> ingresos,
                                                List<Ingreso> pendientes, List<RentabilidadCliente> rentabilidades) {
        Set<Long> ids = new HashSet<>();
        for (Gasto gasto : gastos) {
            if (gasto.getClienteId() != null) {
                ids.add(gasto.getClienteId());
            }
        }
        for (Ingreso ingreso : ingresos) {
            ids.add(ingreso.getClienteId());
        }
        for (Ingreso ingreso : pendientes) {
            ids.add(ingreso.getClienteId());
        }
        for (RentabilidadCliente rentabilidad : rentabilidades) {
            if (rentabilidad.clienteId() != null) {
                ids.add(rentabilidad.clienteId());
            }
        }

        Map<Long, String> nombres = new HashMap<>();
        if (ids.isEmpty()) {
            return nombres;
        }
        for (Cliente cliente : clienteRepository.buscarPorIdsYUsuario(ids, usuarioId)) {
            nombres.put(cliente.getId(), cliente.getNombre());
        }
        return nombres;
    }

    private Map<Long, String> nombresDeCategorias() {
        Map<Long, String> nombres = new HashMap<>();
        for (Categoria categoria : categoriaRepository.buscarTodas()) {
            nombres.put(categoria.getId(), categoria.getNombre());
        }
        return nombres;
    }

    private List<FilaIngreso> filasIngresos(List<Ingreso> ingresos, Map<Long, String> clientes) {
        List<FilaIngreso> filas = new ArrayList<>();
        for (Ingreso i : ingresos) {
            filas.add(new FilaIngreso(i.getFechaEmision(), clientes.get(i.getClienteId()), i.getConcepto(),
                    i.getBaseImponible(), i.getIva(), i.getTotal(), i.getEstadoCobro(), i.getFechaCobro()));
        }
        return filas;
    }

    private List<FilaGasto> filasGastos(List<Gasto> gastos, Map<Long, String> clientes, Map<Long, String> categorias) {
        List<FilaGasto> filas = new ArrayList<>();
        for (Gasto g : gastos) {
            String cliente = null;
            if (g.getClienteId() != null) {
                cliente = clientes.get(g.getClienteId());
            }
            String categoria = null;
            if (g.getCategoriaId() != null) {
                categoria = categorias.get(g.getCategoriaId());
            }
            filas.add(new FilaGasto(g.getFechaEmision(), g.getEmisor(), cliente, categoria,
                    g.getBaseImponible(), g.getIva(), g.getTotal(), g.getEstado(), g.isDeducible()));
        }
        return filas;
    }

    private List<FilaPendiente> filasPendientes(List<Ingreso> pendientes, Map<Long, String> clientes) {
        List<FilaPendiente> filas = new ArrayList<>();
        for (Ingreso i : pendientes) {
            filas.add(new FilaPendiente(i.getFechaEmision(), clientes.get(i.getClienteId()), i.getConcepto(), i.getTotal()));
        }
        return filas;
    }

    private List<FilaRentabilidad> filasRentabilidad(List<RentabilidadCliente> rentabilidades, Map<Long, String> clientes) {
        List<FilaRentabilidad> filas = new ArrayList<>();
        for (RentabilidadCliente r : rentabilidades) {
            if (r.esSinCliente()) {
                filas.add(new FilaRentabilidad(ETIQUETA_SIN_CLIENTE, r.resumen()));
            } else {
                filas.add(new FilaRentabilidad(clientes.get(r.clienteId()), r.resumen()));
            }
        }
        return filas;
    }
}
