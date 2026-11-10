package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.gasto.port.in.DigitalizarFacturaUseCase.ComandoDigitalizarFactura;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.application.shared.port.out.FileStoragePort;
import com.fabio.GestionFacturas.application.shared.port.out.OcrPort;
import com.fabio.GestionFacturas.domain.gasto.ArchivoInvalidoException;
import com.fabio.GestionFacturas.domain.gasto.DatosFacturaExtraidos;
import com.fabio.GestionFacturas.domain.gasto.EstadoGasto;
import com.fabio.GestionFacturas.domain.gasto.FacturaTextParser;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de DigitalizarFacturaService con sus cuatro colaboradores mockeados
 * (FileStoragePort, OcrPort, FacturaTextParser, GastoRepositoryPort). Verifican que el service
 * encadena bien los pasos del pipeline OCR sin tocar almacenamiento, OCR ni BD reales.
 */
@ExtendWith(MockitoExtension.class)
class DigitalizarFacturaServiceTest {

    @Mock
    private FileStoragePort fileStoragePort;
    @Mock
    private OcrPort ocrPort;
    @Mock
    private FacturaTextParser facturaTextParser;
    @Mock
    private GastoRepositoryPort gastoRepositoryPort;

    @InjectMocks
    private DigitalizarFacturaService service;

    @Test
    @DisplayName("almacena el archivo, extrae el texto, lo parsea y guarda un gasto en BORRADOR")
    void digitalizaCorrectamenteConDatosCompletos(){
        //Arrange
        byte[] contenido = "contenido-de-prueba".getBytes();
        ComandoDigitalizarFactura comando = new ComandoDigitalizarFactura(1L, contenido, "factura.png", "image/png");

        when(fileStoragePort.guardar(contenido, "factura.png", "image/png")).thenReturn("uuid-referencia.png");
        when(ocrPort.extraerTexto(contenido)).thenReturn("texto-ocr-simulado");
        when(facturaTextParser.parsear("texto-ocr-simulado")).thenReturn(new DatosFacturaExtraidos(
                "Iberdrola", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), new BigDecimal("21.00"), new BigDecimal("121.00")
        ));
        when(gastoRepositoryPort.guardar(any(Gasto.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        Gasto resultado = service.digitalizar(comando);

        //Assert
        assertThat(resultado.getEstado()).isEqualTo(EstadoGasto.BORRADOR);
        assertThat(resultado.getUsuarioId()).isEqualTo(1L);
        assertThat(resultado.getEmisor()).isEqualTo("Iberdrola");
        assertThat(resultado.getFechaEmision()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(resultado.getBaseImponible().cantidad()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(resultado.getIva().cantidad()).isEqualByComparingTo(new BigDecimal("21.00"));
        assertThat(resultado.getTotal().cantidad()).isEqualByComparingTo(new BigDecimal("121.00"));
        assertThat(resultado.getReferenciaArchivo()).isEqualTo("uuid-referencia.png");

        verify(fileStoragePort).guardar(contenido, "factura.png", "image/png");
        verify(ocrPort).extraerTexto(contenido);
        verify(facturaTextParser).parsear("texto-ocr-simulado");
        verify(gastoRepositoryPort).guardar(any(Gasto.class));
    }

    @Test
    @DisplayName("deja los campos no extraídos en null sin fallar")
    void digitalizaConCamposFaltantesQuedaConNulos(){
        //Arrange
        byte[] contenido = "contenido-de-prueba".getBytes();
        ComandoDigitalizarFactura comando = new ComandoDigitalizarFactura(1L, contenido, "factura.jpg", "image/jpeg");

        when(fileStoragePort.guardar(any(), any(), any())).thenReturn("uuid-referencia.jpg");
        when(ocrPort.extraerTexto(contenido)).thenReturn("texto-ocr-parcial");
        when(facturaTextParser.parsear("texto-ocr-parcial")).thenReturn(new DatosFacturaExtraidos(
                "Proveedor SL", LocalDate.of(2026, 1, 15),
                new BigDecimal("100.00"), null, new BigDecimal("121.00")
        ));
        when(gastoRepositoryPort.guardar(any(Gasto.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        Gasto resultado = service.digitalizar(comando);

        //Assert
        assertThat(resultado.getIva()).isNull();
        assertThat(resultado.getEmisor()).isEqualTo("Proveedor SL");
        assertThat(resultado.getBaseImponible().cantidad()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(resultado.getTotal().cantidad()).isEqualByComparingTo(new BigDecimal("121.00"));
        assertThat(resultado.getReferenciaArchivo()).isEqualTo("uuid-referencia.jpg");
    }

    @Test
    @DisplayName("rechaza un archivo vacío o nulo sin guardar nada")
    void rechazaArchivoVacio(){
        assertThatThrownBy(() -> service.digitalizar(new ComandoDigitalizarFactura(1L, new byte[0], "f.png", "image/png")))
                .isInstanceOf(ArchivoInvalidoException.class).hasMessageContaining("vacío");
        assertThatThrownBy(() -> service.digitalizar(new ComandoDigitalizarFactura(1L, null, "f.png", "image/png")))
                .isInstanceOf(ArchivoInvalidoException.class);
        verifyNoInteractions(fileStoragePort, ocrPort, gastoRepositoryPort);
    }

    @Test
    @DisplayName("rechaza PDF indicando que todavía no está soportado")
    void rechazaPdf(){
        assertThatThrownBy(() -> service.digitalizar(new ComandoDigitalizarFactura(1L, new byte[]{1}, "f.pdf", "application/pdf")))
                .isInstanceOf(ArchivoInvalidoException.class).hasMessageContaining("PDF");
        verifyNoInteractions(fileStoragePort, ocrPort, gastoRepositoryPort);
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/webp", "text/plain", "application/octet-stream"})
    @DisplayName("rechaza otros tipos de contenido")
    void rechazaTiposNoSoportados(String tipo){
        assertThatThrownBy(() -> service.digitalizar(new ComandoDigitalizarFactura(1L, new byte[]{1}, "f", tipo)))
                .isInstanceOf(ArchivoInvalidoException.class);
        verifyNoInteractions(fileStoragePort, ocrPort, gastoRepositoryPort);
    }

    @Test
    @DisplayName("rechaza un content type nulo")
    void rechazaContentTypeNulo(){
        assertThatThrownBy(() -> service.digitalizar(new ComandoDigitalizarFactura(1L, new byte[]{1}, "f", null)))
                .isInstanceOf(ArchivoInvalidoException.class);
        verifyNoInteractions(fileStoragePort, ocrPort, gastoRepositoryPort);
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/jpeg", "image/png"})
    @DisplayName("acepta image/jpeg e image/png")
    void aceptaTiposPermitidos(String tipo){
        byte[] contenido = {1, 2};
        when(fileStoragePort.guardar(contenido, "f", tipo)).thenReturn("ref");
        when(ocrPort.extraerTexto(contenido)).thenReturn("t");
        when(facturaTextParser.parsear("t")).thenReturn(new DatosFacturaExtraidos(null, null, null, null, null));
        when(gastoRepositoryPort.guardar(any(Gasto.class))).thenAnswer(i -> i.getArgument(0));

        Gasto g = service.digitalizar(new ComandoDigitalizarFactura(1L, contenido, "f", tipo));

        assertThat(g.getReferenciaArchivo()).isEqualTo("ref");
    }

    @Test
    @DisplayName("si falla el OCR borra el archivo guardado y relanza el error original")
    void borraArchivoSiFallaOcr(){
        byte[] contenido = {1};
        RuntimeException error = new IllegalStateException("ocr roto");
        when(fileStoragePort.guardar(contenido, "f.png", "image/png")).thenReturn("ref");
        when(ocrPort.extraerTexto(contenido)).thenThrow(error);

        assertThatThrownBy(() -> service.digitalizar(new ComandoDigitalizarFactura(1L, contenido, "f.png", "image/png")))
                .isSameAs(error);

        verify(fileStoragePort).eliminar("ref");
        verify(gastoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("si falla el parser borra el archivo guardado")
    void borraArchivoSiFallaParser(){
        byte[] contenido = {1};
        when(fileStoragePort.guardar(contenido, "f.png", "image/png")).thenReturn("ref");
        when(ocrPort.extraerTexto(contenido)).thenReturn("t");
        when(facturaTextParser.parsear("t")).thenThrow(new IllegalStateException("parser roto"));

        assertThatThrownBy(() -> service.digitalizar(new ComandoDigitalizarFactura(1L, contenido, "f.png", "image/png")))
                .isInstanceOf(IllegalStateException.class);

        verify(fileStoragePort).eliminar("ref");
    }

    @Test
    @DisplayName("si falla guardar el gasto borra el archivo guardado")
    void borraArchivoSiFallaGuardarGasto(){
        byte[] contenido = {1};
        when(fileStoragePort.guardar(contenido, "f.png", "image/png")).thenReturn("ref");
        when(ocrPort.extraerTexto(contenido)).thenReturn("t");
        when(facturaTextParser.parsear("t")).thenReturn(new DatosFacturaExtraidos(null, null, null, null, null));
        when(gastoRepositoryPort.guardar(any(Gasto.class))).thenThrow(new IllegalStateException("bd caída"));

        assertThatThrownBy(() -> service.digitalizar(new ComandoDigitalizarFactura(1L, contenido, "f.png", "image/png")))
                .isInstanceOf(IllegalStateException.class);

        verify(fileStoragePort).eliminar("ref");
    }

    @Test
    @DisplayName("si además falla el borrado, el error original no queda tapado")
    void falloDeLimpiezaNoTapaErrorOriginal(){
        byte[] contenido = {1};
        RuntimeException error = new IllegalStateException("ocr roto");
        when(fileStoragePort.guardar(contenido, "f.png", "image/png")).thenReturn("ref");
        when(ocrPort.extraerTexto(contenido)).thenThrow(error);
        doThrow(new RuntimeException("no se pudo borrar")).when(fileStoragePort).eliminar("ref");

        assertThatThrownBy(() -> service.digitalizar(new ComandoDigitalizarFactura(1L, contenido, "f.png", "image/png")))
                .isSameAs(error);
    }
}
