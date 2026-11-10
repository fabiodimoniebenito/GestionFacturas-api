package com.fabio.GestionFacturas.domain.gasto;

/** Se lanza cuando el archivo subido para digitalizar no es válido (vacío o de un tipo no soportado). GlobalExceptionHandler la mapea a HTTP 400. */
public class ArchivoInvalidoException extends RuntimeException {
    public ArchivoInvalidoException(String message) {
        super(message);
    }
}
