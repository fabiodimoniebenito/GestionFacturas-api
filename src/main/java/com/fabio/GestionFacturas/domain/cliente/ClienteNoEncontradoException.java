package com.fabio.GestionFacturas.domain.cliente;

/** Se lanza cuando el Cliente no existe o no pertenece al usuario. GlobalExceptionHandler la mapea a HTTP 404. */
public class ClienteNoEncontradoException extends RuntimeException {
    public ClienteNoEncontradoException(String message) {
        super(message);
    }
}
