package com.fabio.GestionFacturas.domain.ingreso;

/** Se lanza al violarse una invariante de Ingreso. GlobalExceptionHandler la mapea a HTTP 400. */
public class IngresoInvalidoException extends RuntimeException {
    public IngresoInvalidoException(String message) {
        super(message);
    }
}
