package com.fabio.GestionFacturas.domain.usuario;

/** Se lanza en un login/refresh fallido (contraseña incorrecta, token caducado o revocado). Se mapea a HTTP 401. */
public class CredencialesInvalidadException extends RuntimeException {
    public CredencialesInvalidadException(String message) {
        super(message);
    }
}
