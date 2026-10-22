package com.fabio.GestionFacturas.domain.ingreso;

/** Se lanza cuando el Ingreso no existe o no pertenece al usuario. GlobalExceptionHandler la mapea a HTTP 404. */
public class IngresoNoEncontradoException extends RuntimeException {
    public IngresoNoEncontradoException(String message) {
        super(message);
    }
}
