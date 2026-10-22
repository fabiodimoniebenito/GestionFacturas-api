package com.fabio.GestionFacturas.application.gasto.port.in;

import com.fabio.GestionFacturas.domain.gasto.Gasto;

/**
 * Puerto de entrada del flujo OCR: recibe la imagen de una factura y devuelve un Gasto en BORRADOR.
 * Lo implementa {@code DigitalizarFacturaService}, que encadena {@code FileStoragePort} (guardar archivo)
 * -> {@code OcrPort} (imagen a texto) -> {@code FacturaTextParser} (texto a campos)
 * -> {@code GastoRepositoryPort} (guardar).
 */
public interface DigitalizarFacturaUseCase {

    Gasto digitalizar(ComandoDigitalizarFactura comando);

    /**
     * {@code contenido} es un byte[] y no un MultipartFile: los puertos de aplicación no pueden
     * depender de tipos del framework, así que el controller convierte antes de llamar.
     */
    record ComandoDigitalizarFactura(
            Long usuarioId,
            byte[] contenido,
            String nombreOriginal,
            String contentType
    ) {}

}
