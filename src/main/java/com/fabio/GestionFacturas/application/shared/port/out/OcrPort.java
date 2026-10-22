package com.fabio.GestionFacturas.application.shared.port.out;

/**
 * Puerto de salida: bytes de imagen -> texto plano, nada más. Lo implementan dos adapters:
 * {@code MockOcrAdapter} (por defecto, dev/tests) y {@code TesseractOcrAdapter} (perfil "ocr").
 * Son intercambiables sin que el service sepa cuál está activo.
 */
public interface OcrPort {

    String extraerTexto(byte[] contenido);

}
