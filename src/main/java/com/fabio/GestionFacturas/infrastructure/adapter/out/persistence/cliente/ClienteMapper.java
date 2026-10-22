package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.cliente;

import com.fabio.GestionFacturas.domain.cliente.Cliente;

/**
 * Convierte entre el {@link Cliente} de dominio y {@link ClienteJpaEntity}: el tercero de los
 * tres modelos (DTO / dominio / entidad JPA). Solo lo usa {@code ClientePersistenceAdapter}.
 */
public class ClienteMapper {

    public ClienteMapper() {
    }

    public static ClienteJpaEntity aEntidad(Cliente cliente) {
        return new ClienteJpaEntity(
                cliente.getId(),
                cliente.getUsuarioId(),
                cliente.getNombre(),
                cliente.getNif(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.isActivo()
        );
    }

    public static Cliente aDominio(ClienteJpaEntity entidad) {
        return new Cliente(
                entidad.getId(),
                entidad.getUsuarioId(),
                entidad.getNombre(),
                entidad.getNif(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.isActivo()
        );
    }
}
