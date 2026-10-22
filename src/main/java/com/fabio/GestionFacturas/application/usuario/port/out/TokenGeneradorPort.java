package com.fabio.GestionFacturas.application.usuario.port.out;

/** Puerto de salida para emitir access tokens. Lo implementa {@code JwtTokenProvider} (infrastructure/config/security). */
public interface TokenGeneradorPort {

    String generarAccessToken(Long usuarioId, String email);

}
