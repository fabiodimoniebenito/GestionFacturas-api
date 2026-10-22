package com.fabio.GestionFacturas.domain.usuario;

import java.time.LocalDateTime;

/**
 * Registro de dominio de un refresh token. Solo se guarda el hash (aquí y en persistencia):
 * el token en claro no se conserva, así una filtración de la BD no sirve para falsificar sesiones.
 * La revocación es una marca (no un borrado) para conservar el historial de logout.
 */
public class RefreshToken {


    private final Long id;
    private final Long usuarioId;
    private final String tokenHash;
    private final LocalDateTime expiraEn;
    private boolean revocado;
    private final LocalDateTime creadoEn;

    public RefreshToken(boolean revocado, Long id, Long usuarioId, String tokenHash, LocalDateTime expiraEn, LocalDateTime creadoEn) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El refresh token debe tener un usuario");
        }
        if (tokenHash == null) {
            throw new IllegalArgumentException("El refresh token debe tener un hash");
        }
        this.revocado = revocado;
        this.id = id;
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.expiraEn = expiraEn;

        LocalDateTime fechaCreacion = creadoEn;
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        this.creadoEn = fechaCreacion;
    }

    /** Es válido solo si no está revocado ni caducado; se comprueba en cada refresh/logout. */
    public boolean esValido() {
        if (revocado) {
            return false;
        }
        if (expiraEn.isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    /** Deja el token inutilizable sin borrar la fila (ver nota de la clase). */
    public void revocar() {
        this.revocado = true;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiraEn() { return expiraEn; }
    public boolean isRevocado() { return revocado; }
    public LocalDateTime getCreadoEn() { return creadoEn; }

}
