package com.fabio.GestionFacturas.application.usuario.port.in;

import com.fabio.GestionFacturas.domain.usuario.Usuario;
import com.fabio.GestionFacturas.domain.usuario.Usuario;


/** Puerto de entrada para el registro. Lo implementa {@code RegistrarUsuarioService}, invocado por {@code AuthController}. */
public interface RegistrarUsuarioUseCase {


    Usuario registrar (ComandoRegistrar comando);

    /** La contraseña en claro solo viaja en este comando hasta el service, que la hashea antes de guardarla. */
    record ComandoRegistrar(String email, String nombre, String password){}




}
