package com.fabio.GestionFacturas.application.usuario.port.out;

import java.time.LocalDateTime;

/** Puerto de salida implementado por {@code RefreshTokenGenerador} (infrastructure/config/security). */
public interface RefreshTokenGeneradorPort {

    String generar();

    /** SHA-256 y no BCrypt: al refrescar hay que buscar el token por su hash, y el salt aleatorio de BCrypt lo impediría. */
    String hashear(String tokenPlano);

    LocalDateTime calcularExpiracion();

}
