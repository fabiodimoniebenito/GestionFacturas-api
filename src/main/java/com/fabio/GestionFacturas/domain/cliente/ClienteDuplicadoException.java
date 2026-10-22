package com.fabio.GestionFacturas.domain.cliente;

/** Se lanza cuando el usuario ya tiene un cliente con ese NIF. GlobalExceptionHandler la mapea a HTTP 409. */
public class ClienteDuplicadoException extends RuntimeException {
    public ClienteDuplicadoException(String message) {
        super(message);
    }
}
