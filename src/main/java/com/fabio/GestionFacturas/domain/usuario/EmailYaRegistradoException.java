package com.fabio.GestionFacturas.domain.usuario;

/** Se lanza al registrarse con un email que ya tiene cuenta. Se mapea a HTTP 409. */
public class EmailYaRegistradoException extends RuntimeException {
    public EmailYaRegistradoException(String message) {
        super(message);
    }
}
