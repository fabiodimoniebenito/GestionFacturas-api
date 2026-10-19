package com.fabio.GestionFacturas.domain.gasto;

/** Thrown when an operation is not allowed in the Gasto's current estado. Mapped to HTTP 409 by GlobalExceptionHandler. */
public class GastoEstadoInvalidoException extends RuntimeException {
    public GastoEstadoInvalidoException(String message) {
        super(message);
    }
}
