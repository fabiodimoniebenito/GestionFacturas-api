package com.fabio.GestionFacturas.domain.gasto;

/** Thrown when a Gasto does not exist or belongs to another user. Mapped to HTTP 404 by GlobalExceptionHandler. */
public class GastoNoEncontradoException extends RuntimeException {
    public GastoNoEncontradoException(String message) {
        super(message);
    }
}
