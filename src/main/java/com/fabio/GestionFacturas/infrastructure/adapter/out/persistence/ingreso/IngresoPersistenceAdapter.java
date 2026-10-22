package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso;

import com.fabio.GestionFacturas.application.ingreso.port.out.IngresoRepositoryPort;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementa {@link IngresoRepositoryPort} con Spring Data JPA; la capa de aplicación nunca lo ve directamente.
 * Recorrido completo de una escritura:
 * Ingreso de dominio -> IngresoMapper -> IngresoJpaEntity -> IngresoJpaRepository (Spring Data) -> BD,
 * y se vuelve a mapear a dominio antes de devolver, así quien llama nunca toca el tipo JPA.
 */
@Component
public class IngresoPersistenceAdapter implements IngresoRepositoryPort {

    private final IngresoJpaRepository ingresoJpaRepository;

    public IngresoPersistenceAdapter(IngresoJpaRepository ingresoJpaRepository) {
        this.ingresoJpaRepository = ingresoJpaRepository;
    }

    @Override
    public Ingreso guardar(Ingreso ingreso) {
        IngresoJpaEntity entidad = IngresoMapper.aEntidad(ingreso);
        IngresoJpaEntity guardado = ingresoJpaRepository.save(entidad);
        return IngresoMapper.aDominio(guardado);
    }

    @Override
    public Optional<Ingreso> buscarPorId(Long id) {
        return ingresoJpaRepository.findById(id).map(IngresoMapper::aDominio);
    }

    @Override
    public List<Ingreso> buscarPorUsuario(Long usuarioId) {
        return ingresoJpaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(IngresoMapper::aDominio)
                .toList();
    }
}
