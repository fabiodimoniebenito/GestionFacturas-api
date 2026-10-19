package com.fabio.GestionFacturas.infrastructure.adapter.in.web;

import com.fabio.GestionFacturas.domain.gasto.EstadoGasto;
import com.fabio.GestionFacturas.domain.ingreso.EstadoCobro;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.GastoResponse;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto.IngresoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The DTOs keep enum-like fields as String but declare the allowed values for /v3/api-docs with
 * {@code @Schema(allowableValues)}. These tests fail if that list drifts from the domain enums.
 */
class EnumsDeclaradosEnContratoTest {

    private static String[] valoresPermitidos(Class<?> dto, String componente) throws Exception {
        return dto.getDeclaredField(componente).getAnnotation(Schema.class).allowableValues();
    }

    @Test
    @DisplayName("GastoResponse.estado declara exactamente los valores de EstadoGasto")
    void estadoGastoCoincideConElEnum() throws Exception {
        String[] esperados = Arrays.stream(EstadoGasto.values()).map(Enum::name).toArray(String[]::new);

        assertThat(valoresPermitidos(GastoResponse.class, "estado")).containsExactlyInAnyOrder(esperados);
    }

    @Test
    @DisplayName("IngresoResponse.estadoCobro declara exactamente los valores de EstadoCobro")
    void estadoCobroCoincideConElEnum() throws Exception {
        String[] esperados = Arrays.stream(EstadoCobro.values()).map(Enum::name).toArray(String[]::new);

        assertThat(valoresPermitidos(IngresoResponse.class, "estadoCobro")).containsExactlyInAnyOrder(esperados);
    }
}
