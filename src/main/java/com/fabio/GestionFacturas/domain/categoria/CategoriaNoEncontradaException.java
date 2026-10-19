package com.fabio.GestionFacturas.domain.categoria;

/** Thrown when a referenced Categoria does not exist. Mapped to HTTP 404 by GlobalExceptionHandler. */
public class CategoriaNoEncontradaException extends RuntimeException {
    public CategoriaNoEncontradaException(String message) {
        super(message);
    }
}
