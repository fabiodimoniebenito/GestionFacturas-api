package com.fabio.GestionFacturas.infrastructure.adapter.out.ocr;

/** El motor OCR no está disponible por configuración o entorno (p. ej. falta tessdata o la librería nativa), no por la imagen. GlobalExceptionHandler la mapea a 503. */
public class OcrNoDisponibleException extends OcrException {

    public OcrNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
