package com.fabio.GestionFacturas.application.usuario.port.in;

/** Puerto de entrada para el logout. Lo implementa {@code LogoutService}, que revoca (no borra) el refresh token. */
public interface LogoutUseCase {

    void logout(ComandoLogout comandoLogout);

    record ComandoLogout(String refreshToken){};

}
