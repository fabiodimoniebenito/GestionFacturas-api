package com.fabio.GestionFacturas.domain.gasto;

import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Unit tests for the edit (actualizar) and review (revisar) rules of Gasto — pure domain, no mocks. */
class GastoEdicionRevisionTest {

    private static Dinero eur(String cantidad) {
        return new Dinero(new BigDecimal(cantidad), "EUR");
    }

    private static Gasto gastoEn(EstadoGasto estado, LocalDate fecha, Dinero total) {
        return new Gasto(10L, 1L, null, null, "Proveedor SL", fecha, eur("100.00"), eur("21.00"), total,
                null, false, estado, LocalDateTime.now());
    }

    private static Gasto gastoCompletoEn(EstadoGasto estado) {
        return gastoEn(estado, LocalDate.of(2026, 3, 1), eur("121.00"));
    }

    // ---- actualizar ----

    @Test
    @DisplayName("actualizar reemplaza todos los campos editables")
    void actualizarReemplazaLosCampos() {
        Gasto gasto = gastoCompletoEn(EstadoGasto.BORRADOR);

        gasto.actualizar("Otro SL", LocalDate.of(2026, 4, 2), eur("200.00"), eur("42.00"), eur("242.00"),
                7L, 3L, true);

        assertThat(gasto.getEmisor()).isEqualTo("Otro SL");
        assertThat(gasto.getFechaEmision()).isEqualTo(LocalDate.of(2026, 4, 2));
        assertThat(gasto.getBaseImponible().cantidad()).isEqualByComparingTo("200.00");
        assertThat(gasto.getIva().cantidad()).isEqualByComparingTo("42.00");
        assertThat(gasto.getTotal().cantidad()).isEqualByComparingTo("242.00");
        assertThat(gasto.getClienteId()).isEqualTo(7L);
        assertThat(gasto.getCategoriaId()).isEqualTo(3L);
        assertThat(gasto.isDeducible()).isTrue();
    }

    @Test
    @DisplayName("actualizar con clienteId y categoriaId nulos los desasocia")
    void actualizarPermiteLimpiarClienteYCategoria() {
        Gasto gasto = new Gasto(10L, 1L, 3L, 7L, "Proveedor SL", LocalDate.of(2026, 3, 1), null, null,
                eur("121.00"), null, false, EstadoGasto.BORRADOR, LocalDateTime.now());

        gasto.actualizar("Proveedor SL", LocalDate.of(2026, 3, 1), null, null, eur("121.00"), null, null, false);

        assertThat(gasto.getClienteId()).isNull();
        assertThat(gasto.getCategoriaId()).isNull();
    }

    @Test
    @DisplayName("actualizar no cambia el estado en ningún estado editable")
    void actualizarNoCambiaElEstado() {
        for (EstadoGasto estado : new EstadoGasto[]{EstadoGasto.BORRADOR, EstadoGasto.PROCESADA, EstadoGasto.REVISADA}) {
            Gasto gasto = gastoCompletoEn(estado);

            gasto.actualizar("Otro SL", LocalDate.of(2026, 4, 2), null, null, eur("50.00"), null, null, false);

            assertThat(gasto.getEstado()).isEqualTo(estado);
        }
    }

    @Test
    @DisplayName("actualizar un gasto EXPORTADA lanza GastoEstadoInvalidoException y no modifica nada")
    void actualizarExportadaLanzaExcepcion() {
        Gasto gasto = gastoCompletoEn(EstadoGasto.EXPORTADA);

        assertThatThrownBy(() -> gasto.actualizar("Otro SL", LocalDate.of(2026, 4, 2), null, null,
                eur("50.00"), null, null, false))
                .isInstanceOf(GastoEstadoInvalidoException.class);

        assertThat(gasto.getEmisor()).isEqualTo("Proveedor SL");
        assertThat(gasto.getTotal().cantidad()).isEqualByComparingTo("121.00");
        assertThat(gasto.getEstado()).isEqualTo(EstadoGasto.EXPORTADA);
    }

    @Test
    @DisplayName("actualizar con importes negativos lanza GastoInvalidoException y no modifica nada")
    void actualizarConImportesNegativosLanzaExcepcion() {
        Gasto gasto = gastoCompletoEn(EstadoGasto.BORRADOR);

        assertThatThrownBy(() -> gasto.actualizar("Otro SL", LocalDate.of(2026, 4, 2), eur("-1.00"), null,
                eur("50.00"), null, null, false))
                .isInstanceOf(GastoInvalidoException.class);
        assertThatThrownBy(() -> gasto.actualizar("Otro SL", LocalDate.of(2026, 4, 2), null, eur("-1.00"),
                eur("50.00"), null, null, false))
                .isInstanceOf(GastoInvalidoException.class);
        assertThatThrownBy(() -> gasto.actualizar("Otro SL", LocalDate.of(2026, 4, 2), null, null,
                eur("-50.00"), null, null, false))
                .isInstanceOf(GastoInvalidoException.class);

        assertThat(gasto.getEmisor()).isEqualTo("Proveedor SL");
        assertThat(gasto.getTotal().cantidad()).isEqualByComparingTo("121.00");
    }

    @Test
    @DisplayName("actualizar sin total lanza GastoInvalidoException")
    void actualizarSinTotalLanzaExcepcion() {
        Gasto gasto = gastoCompletoEn(EstadoGasto.BORRADOR);

        assertThatThrownBy(() -> gasto.actualizar("Otro SL", LocalDate.of(2026, 4, 2), null, null,
                null, null, null, false))
                .isInstanceOf(GastoInvalidoException.class);
    }

    // ---- revisar ----

    @Test
    @DisplayName("revisar mueve BORRADOR y PROCESADA a REVISADA")
    void revisarDesdeEstadosPermitidos() {
        for (EstadoGasto estado : new EstadoGasto[]{EstadoGasto.BORRADOR, EstadoGasto.PROCESADA}) {
            Gasto gasto = gastoCompletoEn(estado);

            gasto.revisar();

            assertThat(gasto.getEstado()).isEqualTo(EstadoGasto.REVISADA);
        }
    }

    @Test
    @DisplayName("revisar desde REVISADA o EXPORTADA lanza GastoEstadoInvalidoException")
    void revisarDesdeEstadosNoPermitidos() {
        for (EstadoGasto estado : new EstadoGasto[]{EstadoGasto.REVISADA, EstadoGasto.EXPORTADA}) {
            Gasto gasto = gastoCompletoEn(estado);

            assertThatThrownBy(gasto::revisar).isInstanceOf(GastoEstadoInvalidoException.class);
            assertThat(gasto.getEstado()).isEqualTo(estado);
        }
    }

    @Test
    @DisplayName("revisar sin fechaEmision lanza GastoInvalidoException y deja el estado")
    void revisarSinFechaLanzaExcepcion() {
        Gasto gasto = gastoEn(EstadoGasto.BORRADOR, null, eur("121.00"));

        assertThatThrownBy(gasto::revisar).isInstanceOf(GastoInvalidoException.class);
        assertThat(gasto.getEstado()).isEqualTo(EstadoGasto.BORRADOR);
    }

    @Test
    @DisplayName("revisar sin total lanza GastoInvalidoException y deja el estado")
    void revisarSinTotalLanzaExcepcion() {
        Gasto gasto = gastoEn(EstadoGasto.PROCESADA, LocalDate.of(2026, 3, 1), null);

        assertThatThrownBy(gasto::revisar).isInstanceOf(GastoInvalidoException.class);
        assertThat(gasto.getEstado()).isEqualTo(EstadoGasto.PROCESADA);
    }

    @Test
    @DisplayName("revisar(categoria, deducible) aplica las mismas reglas y no asigna nada si falla")
    void revisarConCategoriaAplicaLasMismasReglas() {
        Gasto gasto = gastoCompletoEn(EstadoGasto.EXPORTADA);

        assertThatThrownBy(() -> gasto.revisar(5L, true)).isInstanceOf(GastoEstadoInvalidoException.class);

        assertThat(gasto.getCategoriaId()).isNull();
        assertThat(gasto.isDeducible()).isFalse();
    }
}
