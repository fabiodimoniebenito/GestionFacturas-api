package com.fabio.GestionFacturas.domain.gasto;

/** Se lanza al violarse una invariante de Gasto. GlobalExceptionHandler la mapea a HTTP 400. */
public class GastoInvalidoException extends RuntimeException {
    public GastoInvalidoException(String message) {
        super(message);
    }
}
