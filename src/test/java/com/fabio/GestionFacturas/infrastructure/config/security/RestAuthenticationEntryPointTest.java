package com.fabio.GestionFacturas.infrastructure.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RestAuthenticationEntryPoint, constructed directly (no Spring context).
 */
class RestAuthenticationEntryPointTest {

    private final RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint();

    private MockHttpServletResponse response;

    @BeforeEach
    void prepararRespuesta() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/gastos");
        response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("token caducado: detalle interno"));
    }

    @Test
    @DisplayName("responde 401 a una petición no autenticada")
    void responde401() {
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("lleva la cabecera WWW-Authenticate: Bearer")
    void llevaWwwAuthenticate() {
        assertThat(response.getHeader("WWW-Authenticate")).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("el cuerpo es JSON con el campo error")
    void cuerpoJsonConError() throws Exception {
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getContentAsString()).startsWith("{\"error\":\"").endsWith("\"}");
    }

    @Test
    @DisplayName("no filtra el motivo del fallo de autenticación")
    void noFiltraDetalles() throws Exception {
        assertThat(response.getContentAsString()).doesNotContain("token caducado").doesNotContain("detalle interno");
    }
}
