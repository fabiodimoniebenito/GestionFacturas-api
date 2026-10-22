package com.fabio.GestionFacturas.application.usuario.port.in;

/**
 * Puerto de entrada para canjear un refresh token válido por un nuevo access token.
 * Lo implementa {@code RefreshTokenService}. Aún sin rotación (aplazada a propósito):
 * el mismo refresh token sigue siendo válido tras cada renovación.
 */
public interface RefreshTokenUseCase {

    ResultadoRenovacion renovar(ComandoRenovar comandoRenovar);

    record ComandoRenovar(String refreshToken){};
    record ResultadoRenovacion(String accessToken){};

}
