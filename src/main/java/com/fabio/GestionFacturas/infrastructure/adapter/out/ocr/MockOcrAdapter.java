package com.fabio.GestionFacturas.infrastructure.adapter.out.ocr;

import com.fabio.GestionFacturas.application.shared.port.out.OcrPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementa {@link OcrPort} con un texto de ejemplo fijo en lugar de un motor OCR real.
 * Es el bean por defecto (activo cuando el perfil "ocr" NO está activo), así dev y tests ejercitan
 * todo el flujo de digitalización sin instalar Tesseract. La implementación real es {@link TesseractOcrAdapter}.
 */
@Component
@Profile("!ocr")
public class MockOcrAdapter implements OcrPort {

    private static final String TEXTO_FACTURA_SIMULADA = """
            Emisor: Iberdrola Comercializacion S.A.U.
            Fecha: 15/01/2026
            Base imponible: 100,00 €
            IVA: 21,00 €
            Total: 121,00 €
            """;

    @Override
    public String extraerTexto(byte[] contenido) {
        return TEXTO_FACTURA_SIMULADA;
    }
}
