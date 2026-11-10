package com.fabio.GestionFacturas.infrastructure.adapter.in.web;

import com.fabio.GestionFacturas.domain.gasto.ArchivoInvalidoException;
import com.fabio.GestionFacturas.infrastructure.adapter.out.ocr.OcrException;
import com.fabio.GestionFacturas.infrastructure.adapter.out.ocr.OcrNoDisponibleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** Mapeos HTTP de los errores del endpoint de digitalización. */
class GlobalExceptionHandlerDigitalizarTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("archivo demasiado grande -> 413 citando el límite de 10 MB")
    void tamanoExcedido() {
        ResponseEntity<Map<String, String>> r = handler.handleTamanoMaximoExcedido(new MaxUploadSizeExceededException(10L * 1024 * 1024));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(r.getBody().get("error")).contains("10 MB");
    }

    @Test
    @DisplayName("falta la parte archivo -> 400 con cuerpo JSON de error")
    void parteFaltante() {
        ResponseEntity<Map<String, String>> r = handler.handleParteFaltante(new MissingServletRequestPartException("archivo"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody()).containsKey("error");
    }

    @Test
    @DisplayName("archivo inválido -> 400 con el mensaje de la excepción")
    void archivoInvalido() {
        ResponseEntity<Map<String, String>> r = handler.handleArchivoInvalido(new ArchivoInvalidoException("PDF no soportado"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody().get("error")).isEqualTo("PDF no soportado");
    }

    @Test
    @DisplayName("OcrException -> 422 con mensaje genérico, sin filtrar el detalle técnico")
    void ocrIlegible() {
        ResponseEntity<Map<String, String>> r = handler.handleOcr(new OcrException("detalle interno secreto"));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(r.getBody().get("error")).isEqualTo("No se pudo leer el contenido de la imagen");
    }

    @Test
    @DisplayName("OCR mal configurado -> 503 con mensaje genérico")
    void ocrNoDisponible() {
        ResponseEntity<Map<String, String>> r = handler.handleOcrNoDisponible(new OcrNoDisponibleException("falta tessdata", new RuntimeException()));
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(r.getBody().get("error")).doesNotContain("tessdata");
    }
}
