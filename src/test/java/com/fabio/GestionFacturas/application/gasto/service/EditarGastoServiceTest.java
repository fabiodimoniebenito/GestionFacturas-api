package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.categoria.port.out.CategoriaRepositoryPort;
import com.fabio.GestionFacturas.application.cliente.port.out.ClienteRepositoryPort;
import com.fabio.GestionFacturas.application.gasto.port.in.EditarGastoUseCase.ComandoEditarGasto;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.categoria.Categoria;
import com.fabio.GestionFacturas.domain.categoria.CategoriaNoEncontradaException;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.domain.cliente.ClienteNoEncontradoException;
import com.fabio.GestionFacturas.domain.gasto.EstadoGasto;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.gasto.GastoNoEncontradoException;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for EditarGastoService with the outbound ports mocked — ownership, category and
 * cliente checks, and that nothing is saved when any of them fails.
 */
@ExtendWith(MockitoExtension.class)
class EditarGastoServiceTest {

    private static final Long USUARIO = 1L;
    private static final Long GASTO_ID = 10L;

    @Mock
    private GastoRepositoryPort gastoRepositoryPort;

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;

    @InjectMocks
    private EditarGastoService service;

    private Gasto gastoDe(Long usuarioId, Long clienteId) {
        return new Gasto(GASTO_ID, usuarioId, null, clienteId, "Proveedor SL", LocalDate.of(2026, 3, 1),
                new Dinero(new BigDecimal("100.00"), "EUR"), new Dinero(new BigDecimal("21.00"), "EUR"),
                new Dinero(new BigDecimal("121.00"), "EUR"), null, false, EstadoGasto.BORRADOR,
                LocalDateTime.now());
    }

    private ComandoEditarGasto comando(Long usuarioId, Long clienteId, Long categoriaId) {
        return new ComandoEditarGasto(GASTO_ID, usuarioId, "Otro SL", LocalDate.of(2026, 4, 2),
                new BigDecimal("200.00"), new BigDecimal("42.00"), new BigDecimal("242.00"), null,
                clienteId, categoriaId, true);
    }

    @Test
    @DisplayName("editar un gasto inexistente lanza GastoNoEncontradoException y no guarda")
    void editarInexistente() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.editar(comando(USUARIO, null, null)))
                .isInstanceOf(GastoNoEncontradoException.class);

        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("editar el gasto de otro usuario lanza GastoNoEncontradoException y no guarda")
    void editarGastoDeOtroUsuario() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.of(gastoDe(2L, null)));

        assertThatThrownBy(() -> service.editar(comando(USUARIO, null, null)))
                .isInstanceOf(GastoNoEncontradoException.class);

        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("editar con una categoría inexistente lanza CategoriaNoEncontradaException y no guarda")
    void editarConCategoriaInexistente() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.of(gastoDe(USUARIO, null)));
        when(categoriaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.editar(comando(USUARIO, null, 99L)))
                .isInstanceOf(CategoriaNoEncontradaException.class);

        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("al cambiar el cliente se valida como en la creación (de otro usuario -> excepción, sin guardar)")
    void editarCambiandoAClienteDeOtroUsuario() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.of(gastoDe(USUARIO, null)));
        when(clienteRepositoryPort.buscarPorId(5L))
                .thenReturn(Optional.of(new Cliente(5L, 2L, "Cliente SL", "B11111111", null, null, true)));

        assertThatThrownBy(() -> service.editar(comando(USUARIO, 5L, null)))
                .isInstanceOf(ClienteNoEncontradoException.class);

        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("al cambiar a un cliente propio y activo se asocia y se guarda")
    void editarCambiandoAClienteValido() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.of(gastoDe(USUARIO, null)));
        when(clienteRepositoryPort.buscarPorId(5L))
                .thenReturn(Optional.of(new Cliente(5L, USUARIO, "Cliente SL", "B11111111", null, null, true)));
        when(gastoRepositoryPort.guardar(any(Gasto.class))).thenAnswer(inv -> inv.getArgument(0));

        Gasto resultado = service.editar(comando(USUARIO, 5L, null));

        assertThat(resultado.getClienteId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("si el cliente no cambia no se vuelve a validar")
    void editarConClienteSinCambiarNoValida() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.of(gastoDe(USUARIO, 5L)));
        when(gastoRepositoryPort.guardar(any(Gasto.class))).thenAnswer(inv -> inv.getArgument(0));

        Gasto resultado = service.editar(comando(USUARIO, 5L, null));

        verifyNoInteractions(clienteRepositoryPort);
        assertThat(resultado.getClienteId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("clienteId nulo desasocia el cliente sin validar nada")
    void editarConClienteNuloLoLimpia() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.of(gastoDe(USUARIO, 5L)));
        when(gastoRepositoryPort.guardar(any(Gasto.class))).thenAnswer(inv -> inv.getArgument(0));

        Gasto resultado = service.editar(comando(USUARIO, null, null));

        verifyNoInteractions(clienteRepositoryPort);
        assertThat(resultado.getClienteId()).isNull();
    }

    @Test
    @DisplayName("camino feliz: aplica los cambios, usa EUR por defecto, mantiene el estado y guarda")
    void editarCaminoFeliz() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.of(gastoDe(USUARIO, null)));
        when(categoriaRepositoryPort.buscarPorId(3L)).thenReturn(Optional.of(new Categoria(3L, "Transporte", false)));
        when(gastoRepositoryPort.guardar(any(Gasto.class))).thenAnswer(inv -> inv.getArgument(0));

        Gasto resultado = service.editar(comando(USUARIO, null, 3L));

        assertThat(resultado.getEmisor()).isEqualTo("Otro SL");
        assertThat(resultado.getTotal().cantidad()).isEqualByComparingTo("242.00");
        assertThat(resultado.getTotal().moneda()).isEqualTo("EUR");
        assertThat(resultado.getCategoriaId()).isEqualTo(3L);
        assertThat(resultado.isDeducible()).isTrue();
        assertThat(resultado.getEstado()).isEqualTo(EstadoGasto.BORRADOR);
        verify(gastoRepositoryPort).guardar(resultado);
    }
}
