package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.cliente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Modelo de persistencia de Cliente: entidad JPA simple, separada del
 * {@link com.fabio.GestionFacturas.domain.cliente.Cliente} de dominio. {@code ClienteMapper} convierte
 * entre ambos; esta clase nunca sale del adapter de persistencia.
 */
@Entity
@Table(name = "cliente")
public class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String nif;

    private String email;

    private String telefono;

    @Column(nullable = false)
    private boolean activo;

    // insertable/updatable = false: lo fija un valor por defecto de la BD y nunca se escribe desde Java
    // (igual que en UsuarioJpaEntity; el objeto de dominio tampoco lleva este campo)
    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    /** Constructor sin argumentos exigido por JPA/Hibernate. */
    protected ClienteJpaEntity() {
    }

    public ClienteJpaEntity(Long id, Long usuarioId, String nombre, String nif, String email,
                             String telefono, boolean activo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.nif = nif;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNif() {
        return nif;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
