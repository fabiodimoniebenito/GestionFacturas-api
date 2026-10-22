package com.fabio.GestionFacturas.domain.cliente;

/**
 * Entidad de dominio: un cliente de un usuario, guardado solo para organización (esta app no le factura
 * directamente). POJO puro a propósito (arquitectura hexagonal): sin anotaciones de JPA/Spring;
 * el mapeo a persistencia vive en infraestructura (JpaEntity + Mapper).
 */
public class Cliente {

    private final Long id;
    private final Long usuarioId;
    private String nombre;
    private final String nif;
    private String email;
    private String telefono;
    private boolean activo;

    /**
     * Constructor completo, usado por los mappers para reconstruir un Cliente desde persistencia.
     * Valida las invariantes del dominio por sí mismo, sin depender de ningún framework.
     */
    public Cliente(Long id, Long usuarioId, String nombre, String nif, String email,
                    String telefono, boolean activo) {
        if (usuarioId == null) {
            throw new ClienteInvalidoException("El cliente debe tener un usuario");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ClienteInvalidoException("El cliente debe tener un nombre");
        }
        if (nif == null || nif.isBlank()) {
            throw new ClienteInvalidoException("El cliente debe tener un NIF");
        }

        this.id = id;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.nif = nif;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
    }

    /** Fábrica para un cliente recién registrado: nace activo. */
    public static Cliente crearActivo(Long usuarioId, String nombre, String nif, String email, String telefono) {
        return new Cliente(null, usuarioId, nombre, nif, email, telefono, true);
    }

    /** Borrado lógico: pasa el cliente a inactivo sin borrar la fila (se conserva el historial de sus gastos). */
    public void desactivar() {
        this.activo = false;
    }

    /** Actualiza los campos modificables (nombre, email, telefono). nif y usuarioId no cambian tras la creación. */
    public void actualizarDatos(String nombre, String email, String telefono) {
        if (nombre == null || nombre.isBlank()) {
            throw new ClienteInvalidoException("El cliente debe tener un nombre");
        }

        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getNombre() { return nombre; }
    public String getNif() { return nif; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public boolean isActivo() { return activo; }
}
