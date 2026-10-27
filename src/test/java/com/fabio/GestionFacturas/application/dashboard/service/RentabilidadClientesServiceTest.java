package com.fabio.GestionFacturas.application.dashboard.service;

import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.application.dashboard.port.in.ObtenerRentabilidadClientesUseCase.ComandoObtenerRentabilidad;
import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraRentabilidadClientes;
import com.fabio.GestionFacturas.domain.dashboard.RentabilidadCliente;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** Tests unitarios de RentabilidadClientesService: orquestación y resolución de nombres; las reglas de cálculo están en CalculadoraRentabilidadClientesTest. */
@ExtendWith(MockitoExtension.class)
class RentabilidadClientesServiceTest {

    private static final LocalDate DESDE = LocalDate.of(2026, 1, 1);
    private static final LocalDate HASTA = LocalDate.of(2026, 3, 31);

    @Mock
    private DashboardConsultaPort dashboardConsulta;

    @Mock
    private CalculadoraRentabilidadClientes calculadora;

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @InjectMocks
    private RentabilidadClientesService service;

    private Dinero eur(String cantidad) {
        return Dinero.deEuros(new BigDecimal(cantidad));
    }

    private Cliente cliente(Long id, String nombre, boolean activo) {
        return new Cliente(id, 1L, nombre, "NIF-" + id, null, null, activo);
    }

    private void stubDatos(List<Gasto> gastos, List<Ingreso> ingresos, List<RentabilidadCliente> calculado) {
        when(dashboardConsulta.buscarGastosPorPeriodo(1L, DESDE, HASTA)).thenReturn(gastos);
        when(dashboardConsulta.buscarIngresosPorPeriodo(1L, DESDE, HASTA)).thenReturn(ingresos);
        when(calculadora.calcular(gastos, ingresos)).thenReturn(calculado);
    }

    @Test
    @DisplayName("obtenerRentabilidad pasa los gastos e ingresos del usuario y periodo a la calculadora y rellena el nombre de cada cliente")
    void obtenerRentabilidadDelegaEnLaCalculadoraYResuelveNombres() {
        // Arrange
        List<Gasto> gastos = List.of(
                Gasto.crearBorrador(1L, 10L, "Proveedor", DESDE, eur("50.00"), eur("10.50"), eur("60.50")));
        List<Ingreso> ingresos = List.of(
                Ingreso.crear(1L, 10L, "Concepto", DESDE, eur("100.00"), eur("21.00"), eur("121.00")));
        stubDatos(gastos, ingresos, List.of(new RentabilidadCliente(10L, null)));
        when(clienteRepository.buscarPorIdsYUsuario(any(), eq(1L)))
                .thenReturn(List.of(cliente(10L, "Acme", true)));

        // Act
        List<RentabilidadCliente> resultado =
                service.obtenerRentabilidad(new ComandoObtenerRentabilidad(1L, DESDE, HASTA));

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).clienteId()).isEqualTo(10L);
        assertThat(resultado.get(0).clienteNombre()).isEqualTo("Acme");
        verify(dashboardConsulta).buscarGastosPorPeriodo(1L, DESDE, HASTA);
        verify(dashboardConsulta).buscarIngresosPorPeriodo(1L, DESDE, HASTA);
        verify(calculadora).calcular(gastos, ingresos);
    }

    @Test
    @DisplayName("obtenerRentabilidad resuelve los nombres de varios clientes con UNA sola consulta por lote, solo del usuario")
    void obtenerRentabilidadResuelveNombresEnUnaSolaConsulta() {
        // Arrange
        stubDatos(List.of(), List.of(), List.of(
                new RentabilidadCliente(10L, null),
                new RentabilidadCliente(20L, null),
                new RentabilidadCliente(30L, null)));
        when(clienteRepository.buscarPorIdsYUsuario(any(), eq(1L))).thenReturn(List.of(
                cliente(10L, "Acme", true),
                cliente(20L, "Beta", true),
                cliente(30L, "Gamma", true)));

        // Act
        List<RentabilidadCliente> resultado =
                service.obtenerRentabilidad(new ComandoObtenerRentabilidad(1L, DESDE, HASTA));

        // Assert
        assertThat(resultado).extracting(RentabilidadCliente::clienteNombre)
                .containsExactly("Acme", "Beta", "Gamma");
        verify(clienteRepository, times(1)).buscarPorIdsYUsuario(eq(Set.of(10L, 20L, 30L)), eq(1L));
        verifyNoMoreInteractions(clienteRepository);
    }

    @Test
    @DisplayName("obtenerRentabilidad incluye el nombre de un cliente INACTIVO")
    void obtenerRentabilidadIncluyeClientesInactivos() {
        // Arrange
        stubDatos(List.of(), List.of(), List.of(new RentabilidadCliente(10L, null)));
        when(clienteRepository.buscarPorIdsYUsuario(any(), eq(1L)))
                .thenReturn(List.of(cliente(10L, "Cliente dado de baja", false)));

        // Act
        List<RentabilidadCliente> resultado =
                service.obtenerRentabilidad(new ComandoObtenerRentabilidad(1L, DESDE, HASTA));

        // Assert
        assertThat(resultado.get(0).clienteNombre()).isEqualTo("Cliente dado de baja");
    }

    @Test
    @DisplayName("obtenerRentabilidad deja clienteId y clienteNombre null en la entrada 'sin cliente' y no la incluye en la consulta de nombres")
    void obtenerRentabilidadMantieneSinClienteComoNull() {
        // Arrange
        stubDatos(List.of(), List.of(), List.of(
                new RentabilidadCliente(10L, null),
                new RentabilidadCliente(null, null)));
        when(clienteRepository.buscarPorIdsYUsuario(any(), eq(1L)))
                .thenReturn(List.of(cliente(10L, "Acme", true)));

        // Act
        List<RentabilidadCliente> resultado =
                service.obtenerRentabilidad(new ComandoObtenerRentabilidad(1L, DESDE, HASTA));

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).clienteNombre()).isEqualTo("Acme");
        assertThat(resultado.get(1).clienteId()).isNull();
        assertThat(resultado.get(1).clienteNombre()).isNull();
        verify(clienteRepository).buscarPorIdsYUsuario(eq(Set.of(10L)), eq(1L));
    }

    @Test
    @DisplayName("obtenerRentabilidad no consulta clientes si solo hay entrada 'sin cliente'")
    void obtenerRentabilidadSinClientesNoConsultaNombres() {
        // Arrange
        stubDatos(List.of(), List.of(), List.of(new RentabilidadCliente(null, null)));

        // Act
        List<RentabilidadCliente> resultado =
                service.obtenerRentabilidad(new ComandoObtenerRentabilidad(1L, DESDE, HASTA));

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).clienteNombre()).isNull();
        verifyNoInteractions(clienteRepository);
    }

    @Test
    @DisplayName("obtenerRentabilidad con resultado vacío devuelve lista vacía sin consultar clientes")
    void obtenerRentabilidadVaciaNoConsultaNombres() {
        // Arrange
        stubDatos(List.of(), List.of(), List.of());

        // Act
        List<RentabilidadCliente> resultado =
                service.obtenerRentabilidad(new ComandoObtenerRentabilidad(1L, DESDE, HASTA));

        // Assert
        assertThat(resultado).isEmpty();
        verifyNoInteractions(clienteRepository);
    }

    @Test
    @DisplayName("obtenerRentabilidad deja el nombre en null si el cliente no se encuentra, sin fallar")
    void obtenerRentabilidadClienteDesconocidoDejaNombreNull() {
        // Arrange
        stubDatos(List.of(), List.of(), List.of(new RentabilidadCliente(99L, null)));
        when(clienteRepository.buscarPorIdsYUsuario(any(), eq(1L))).thenReturn(List.of());

        // Act
        List<RentabilidadCliente> resultado =
                service.obtenerRentabilidad(new ComandoObtenerRentabilidad(1L, DESDE, HASTA));

        // Assert
        assertThat(resultado.get(0).clienteId()).isEqualTo(99L);
        assertThat(resultado.get(0).clienteNombre()).isNull();
    }

    @Test
    @DisplayName("obtenerRentabilidad con 'desde' posterior a 'hasta' lanza IllegalArgumentException sin consultar nada")
    void obtenerRentabilidadRechazaRangoInvertido() {
        // Act + Assert
        assertThatThrownBy(() -> service.obtenerRentabilidad(new ComandoObtenerRentabilidad(1L, HASTA, DESDE)))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(dashboardConsulta, calculadora, clienteRepository);
    }
}
