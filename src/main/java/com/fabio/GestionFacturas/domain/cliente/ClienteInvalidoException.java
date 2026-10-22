package com.fabio.GestionFacturas.domain.cliente;

/** Se lanza al violarse una invariante de Cliente. GlobalExceptionHandler la mapea a HTTP 400. */
public class ClienteInvalidoException extends RuntimeException {
    public ClienteInvalidoException(String message) {
        super(message);
    }
}
