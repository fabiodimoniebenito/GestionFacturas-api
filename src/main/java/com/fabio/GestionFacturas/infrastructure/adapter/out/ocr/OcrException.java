package com.fabio.GestionFacturas.infrastructure.adapter.out.ocr;

/** Se lanza desde {@link TesseractOcrAdapter} ante una entrada ilegible o no soportada, o un fallo del motor. GlobalExceptionHandler no la mapea: acaba en un 500 por defecto. */
public class OcrException extends RuntimeException {

    public OcrException(String message) {
        super(message);
    }

    public OcrException(String message, Throwable cause) {
        super(message, cause);
    }
}
