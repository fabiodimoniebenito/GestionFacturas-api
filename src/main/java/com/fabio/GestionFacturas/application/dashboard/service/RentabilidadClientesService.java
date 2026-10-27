package com.fabio.GestionFacturas.application.dashboard.service;

import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.application.dashboard.port.in.ObtenerRentabilidadClientesUseCase;
import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraRentabilidadClientes;
import com.fabio.GestionFacturas.domain.dashboard.RentabilidadCliente;
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
 * Implementa {@link ObtenerRentabilidadClientesUseCase}: carga los gastos e ingresos del usuario en el
 * periodo, delega las cifras por cliente en {@link CalculadoraRentabilidadClientes} y después resuelve
 * el nombre de cada cliente (activo o inactivo, solo del usuario) con una única consulta por lote.
 */
@Service
public class RentabilidadClientesService implements ObtenerRentabilidadClientesUseCase {

    private final DashboardConsultaPort dashboardConsulta;
    private final CalculadoraRentabilidadClientes calculadora;
    private final ClienteRepositoryPort clienteRepository;

    public RentabilidadClientesService(DashboardConsultaPort dashboardConsulta,
                                       CalculadoraRentabilidadClientes calculadora,
                                       ClienteRepositoryPort clienteRepository) {
        this.dashboardConsulta = dashboardConsulta;
        this.calculadora = calculadora;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentabilidadCliente> obtenerRentabilidad(ComandoObtenerRentabilidad comando) {
        if (comando.desde().isAfter(comando.hasta())) {
            throw new IllegalArgumentException("La fecha 'desde' no puede ser posterior a 'hasta'");
        }

        List<Gasto> gastos = dashboardConsulta.buscarGastosPorPeriodo(
                comando.usuarioId(), comando.desde(), comando.hasta());
        List<Ingreso> ingresos = dashboardConsulta.buscarIngresosPorPeriodo(
                comando.usuarioId(), comando.desde(), comando.hasta());

        List<RentabilidadCliente> rentabilidades = calculadora.calcular(gastos, ingresos);
        return conNombres(comando.usuarioId(), rentabilidades);
    }

    /** La entrada "sin cliente" (clienteId null) no se consulta y conserva el nombre null. */
    private List<RentabilidadCliente> conNombres(Long usuarioId, List<RentabilidadCliente> rentabilidades) {
        Set<Long> clienteIds = new HashSet<>();
        for (RentabilidadCliente rentabilidad : rentabilidades) {
            if (rentabilidad.clienteId() != null) {
                clienteIds.add(rentabilidad.clienteId());
            }
        }
        if (clienteIds.isEmpty()) {
            return rentabilidades;
        }

        Map<Long, String> nombres = new HashMap<>();
        for (Cliente cliente : clienteRepository.buscarPorIdsYUsuario(clienteIds, usuarioId)) {
            nombres.put(cliente.getId(), cliente.getNombre());
        }

        List<RentabilidadCliente> resultado = new ArrayList<>();
        for (RentabilidadCliente rentabilidad : rentabilidades) {
            if (rentabilidad.clienteId() == null) {
                resultado.add(rentabilidad);
            } else {
                resultado.add(rentabilidad.conNombre(nombres.get(rentabilidad.clienteId())));
            }
        }
        return resultado;
    }
}
