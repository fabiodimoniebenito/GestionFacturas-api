package com.fabio.GestionFacturas.application.exportacion.service;

import com.fabio.GestionFacturas.application.categoria.port.out.CategoriaRepositoryPort;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.application.dashboard.port.out.DashboardConsultaPort;
import com.fabio.GestionFacturas.application.exportacion.port.in.ExportarPeriodoUseCase.ComandoExportarPeriodo;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion;
import com.fabio.GestionFacturas.application.shared.port.out.ExportPort;
import com.fabio.GestionFacturas.domain.categoria.Categoria;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraPendientesCobro;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraRentabilidadClientes;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraResumenPeriodo;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Tests de orquestación de ExportarPeriodoService; usa las calculadoras reales (dominio puro) para comprobar que las cifras son las del dashboard. */
@ExtendWith(MockitoExtension.class)
class ExportarPeriodoServiceTest {

    private static final LocalDate DESDE = LocalDate.of(2026, 1, 1);
    private static final LocalDate HASTA = LocalDate.of(2026, 3, 31);

    @Mock
    private DashboardConsultaPort dashboardConsulta;
    @Mock
    private ClienteRepositoryPort clienteRepository;
    @Mock
    private CategoriaRepositoryPort categoriaRepository;
    @Mock
    private ExportPort exportPort;

    private ExportarPeriodoService service;

    @BeforeEach
    void preparar() {
        CalculadoraResumenPeriodo resumen = new CalculadoraResumenPeriodo();
        service = new ExportarPeriodoService(dashboardConsulta, clienteRepository, categoriaRepository,
                resumen, new CalculadoraRentabilidadClientes(resumen), new CalculadoraPendientesCobro(), exportPort);
    }

    private Dinero eur(String cantidad) {
        return Dinero.deEuros(new BigDecimal(cantidad));
    }

    @Test
    @DisplayName("reúne los datos del periodo, resuelve nombres (también de clientes inactivos) y delega en el puerto")
    void reuneDatos() {
        // Arrange
        Ingreso ingreso = Ingreso.crear(1L, 10L, "Web", LocalDate.of(2026, 2, 1), eur("100.00"), eur("21.00"), eur("121.00"));
        Gasto conCliente = Gasto.crearBorrador(1L, 10L, "Proveedor", LocalDate.of(2026, 2, 2), eur("10.00"), eur("2.10"), eur("12.10"));
        conCliente.revisar(5L, false);
        Gasto sinCliente = Gasto.crearBorrador(1L, null, "Luz", LocalDate.of(2026, 2, 3), eur("20.00"), eur("4.20"), eur("24.20"));

        when(dashboardConsulta.buscarGastosPorPeriodo(1L, DESDE, HASTA)).thenReturn(List.of(conCliente, sinCliente));
        when(dashboardConsulta.buscarIngresosPorPeriodo(1L, DESDE, HASTA)).thenReturn(List.of(ingreso));
        when(dashboardConsulta.buscarIngresosPendientesDeCobro(1L)).thenReturn(List.of(ingreso));
        when(clienteRepository.buscarPorIdsYUsuario(any(Collection.class), org.mockito.ArgumentMatchers.eq(1L)))
                .thenReturn(List.of(new Cliente(10L, 1L, "Cliente Inactivo", "NIF-10", null, null, false)));
        when(categoriaRepository.buscarTodas()).thenReturn(List.of(new Categoria(5L, "Software", false)));
        when(exportPort.generarExcel(any())).thenReturn(new byte[]{1, 2, 3});

        // Act
        byte[] resultado = service.exportar(new ComandoExportarPeriodo(1L, DESDE, HASTA));

        // Assert
        assertThat(resultado).containsExactly(1, 2, 3);
        ArgumentCaptor<DatosExportacion> captor = ArgumentCaptor.forClass(DatosExportacion.class);
        verify(exportPort).generarExcel(captor.capture());
        DatosExportacion datos = captor.getValue();

        assertThat(datos.desde()).isEqualTo(DESDE);
        assertThat(datos.hasta()).isEqualTo(HASTA);
        assertThat(datos.resumen().facturadoTotal().cantidad()).isEqualByComparingTo("121.00");
        assertThat(datos.resumen().gastosTotal().cantidad()).isEqualByComparingTo("36.30");
        assertThat(datos.ingresos()).hasSize(1);
        assertThat(datos.ingresos().get(0).clienteNombre()).isEqualTo("Cliente Inactivo");
        assertThat(datos.gastos()).hasSize(2);
        assertThat(datos.gastos().get(0).categoriaNombre()).isEqualTo("Software");
        assertThat(datos.gastos().get(1).clienteNombre()).isNull();
        assertThat(datos.pendientes()).hasSize(1);
        assertThat(datos.totalPendiente().cantidad()).isEqualByComparingTo("121.00");
        assertThat(datos.rentabilidad()).hasSize(2);
        assertThat(datos.rentabilidad().get(0).nombre()).isEqualTo("Cliente Inactivo");
        assertThat(datos.rentabilidad().get(1).nombre()).isEqualTo("Gastos generales (sin cliente)");
    }

    @Test
    @DisplayName("rechaza desde > hasta sin consultar nada ni generar fichero")
    void rechazaRangoInvertido() {
        assertThatThrownBy(() -> service.exportar(new ComandoExportarPeriodo(1L, HASTA, DESDE)))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(dashboardConsulta, clienteRepository, categoriaRepository, exportPort);
    }

    @Test
    @DisplayName("todas las consultas van acotadas al usuario autenticado y los clientes ajenos no aportan nombre")
    void soloDatosDelUsuario() {
        // Arrange: el repositorio no devuelve el cliente 99 porque no es del usuario 1
        Ingreso ingreso = Ingreso.crear(1L, 99L, "Ajeno", LocalDate.of(2026, 2, 1), eur("10.00"), eur("2.10"), eur("12.10"));
        when(dashboardConsulta.buscarGastosPorPeriodo(1L, DESDE, HASTA)).thenReturn(List.of());
        when(dashboardConsulta.buscarIngresosPorPeriodo(1L, DESDE, HASTA)).thenReturn(List.of(ingreso));
        when(dashboardConsulta.buscarIngresosPendientesDeCobro(1L)).thenReturn(List.of());
        when(clienteRepository.buscarPorIdsYUsuario(any(Collection.class), org.mockito.ArgumentMatchers.eq(1L)))
                .thenReturn(List.of());
        when(categoriaRepository.buscarTodas()).thenReturn(List.of());
        when(exportPort.generarExcel(any())).thenReturn(new byte[0]);

        // Act
        service.exportar(new ComandoExportarPeriodo(1L, DESDE, HASTA));

        // Assert
        verify(dashboardConsulta).buscarGastosPorPeriodo(1L, DESDE, HASTA);
        verify(dashboardConsulta).buscarIngresosPorPeriodo(1L, DESDE, HASTA);
        verify(dashboardConsulta).buscarIngresosPendientesDeCobro(1L);
        verify(clienteRepository).buscarPorIdsYUsuario(any(Collection.class), org.mockito.ArgumentMatchers.eq(1L));
        ArgumentCaptor<DatosExportacion> captor = ArgumentCaptor.forClass(DatosExportacion.class);
        verify(exportPort).generarExcel(captor.capture());
        assertThat(captor.getValue().ingresos().get(0).clienteNombre()).isNull();
    }

    @Test
    @DisplayName("periodo vacío: listas vacías, sin consultar clientes")
    void periodoVacio() {
        when(dashboardConsulta.buscarGastosPorPeriodo(1L, DESDE, HASTA)).thenReturn(List.of());
        when(dashboardConsulta.buscarIngresosPorPeriodo(1L, DESDE, HASTA)).thenReturn(List.of());
        when(dashboardConsulta.buscarIngresosPendientesDeCobro(1L)).thenReturn(List.of());
        when(categoriaRepository.buscarTodas()).thenReturn(List.of());
        when(exportPort.generarExcel(any())).thenReturn(new byte[0]);

        service.exportar(new ComandoExportarPeriodo(1L, DESDE, HASTA));

        ArgumentCaptor<DatosExportacion> captor = ArgumentCaptor.forClass(DatosExportacion.class);
        verify(exportPort).generarExcel(captor.capture());
        assertThat(captor.getValue().ingresos()).isEmpty();
        assertThat(captor.getValue().gastos()).isEmpty();
        assertThat(captor.getValue().rentabilidad()).isEmpty();
        verifyNoInteractions(clienteRepository);
    }
}
