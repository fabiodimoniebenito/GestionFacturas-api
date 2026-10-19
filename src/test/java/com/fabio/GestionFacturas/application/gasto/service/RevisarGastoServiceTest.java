package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.domain.gasto.EstadoGasto;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.gasto.GastoEstadoInvalidoException;
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
import static org.mockito.Mockito.when;

/** Unit tests for RevisarGastoService with the outbound port mocked. */
@ExtendWith(MockitoExtension.class)
class RevisarGastoServiceTest {

    private static final Long USUARIO = 1L;
    private static final Long GASTO_ID = 10L;

    @Mock
    private GastoRepositoryPort gastoRepositoryPort;

    @InjectMocks
    private RevisarGastoService service;

    private Gasto gastoDe(Long usuarioId, EstadoGasto estado) {
        return new Gasto(GASTO_ID, usuarioId, null, null, "Proveedor SL", LocalDate.of(2026, 3, 1), null, null,
                new Dinero(new BigDecimal("121.00"), "EUR"), null, false, estado, LocalDateTime.now());
    }

    @Test
    @DisplayName("revisar un gasto inexistente lanza GastoNoEncontradoException y no guarda")
    void revisarInexistente() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.revisar(GASTO_ID, USUARIO))
                .isInstanceOf(GastoNoEncontradoException.class);

        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("revisar el gasto de otro usuario lanza GastoNoEncontradoException y no guarda")
    void revisarGastoDeOtroUsuario() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.of(gastoDe(2L, EstadoGasto.BORRADOR)));

        assertThatThrownBy(() -> service.revisar(GASTO_ID, USUARIO))
                .isInstanceOf(GastoNoEncontradoException.class);

        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("revisar un gasto ya REVISADA lanza GastoEstadoInvalidoException y no guarda")
    void revisarYaRevisada() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.of(gastoDe(USUARIO, EstadoGasto.REVISADA)));

        assertThatThrownBy(() -> service.revisar(GASTO_ID, USUARIO))
                .isInstanceOf(GastoEstadoInvalidoException.class);

        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("camino feliz: BORRADOR pasa a REVISADA y se guarda")
    void revisarCaminoFeliz() {
        when(gastoRepositoryPort.buscarPorId(GASTO_ID)).thenReturn(Optional.of(gastoDe(USUARIO, EstadoGasto.BORRADOR)));
        when(gastoRepositoryPort.guardar(any(Gasto.class))).thenAnswer(inv -> inv.getArgument(0));

        Gasto resultado = service.revisar(GASTO_ID, USUARIO);

        assertThat(resultado.getEstado()).isEqualTo(EstadoGasto.REVISADA);
        verify(gastoRepositoryPort).guardar(resultado);
    }
}
