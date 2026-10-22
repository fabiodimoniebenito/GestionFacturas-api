package com.fabio.GestionFacturas.application.usuario.port.in;

/** Puerto de entrada para el login. Lo implementa {@code AutenticarService}, que verifica credenciales y emite tokens. */
public interface AutenticarUseCase {


    ResultadoAutenticacion autenticar(ComandoLogin comandoLogin);

    record ComandoLogin(String email, String password){};
    /** accessToken es un JWT autocontenido (no se guarda); refreshToken se devuelve en claro una sola vez y solo se persiste su hash. */
    record ResultadoAutenticacion(String accessToken, String refreshToken, Long usuarioId, String email){};

}
