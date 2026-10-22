package com.fabio.GestionFacturas.infrastructure.adapter.out.storage;

/** Se lanza desde {@link LocalStorageAdapter} ante un fallo de E/S. GlobalExceptionHandler la mapea a HTTP 500. */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
