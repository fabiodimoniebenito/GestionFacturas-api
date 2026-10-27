package com.fabio.GestionFacturas.application.cliente.port.out;

import com.fabio.GestionFacturas.domain.cliente.Cliente;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Cliente. Está en la capa de aplicación para que los services
 * dependan solo de este contrato; {@code ClientePersistenceAdapter} (infraestructura) lo implementa con JPA.
 */
public interface ClienteRepositoryPort {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(Long id);

    /** Solo clientes activos: la consulta del adapter ya excluye los inactivos. */
    List<Cliente> buscarPorUsuario(Long usuarioId);

    /**
     * Carga de golpe los clientes del usuario con esos ids, activos E inactivos (a diferencia de
     * {@link #buscarPorUsuario}); los ids que no existan o sean de otro usuario simplemente no aparecen.
     */
    List<Cliente> buscarPorIdsYUsuario(Collection<Long> ids, Long usuarioId);

    /** Permite aplicar la regla de un NIF por usuario antes de crear un cliente. */
    Optional<Cliente> buscarPorUsuarioYNif(Long usuarioId, String nif);
}
