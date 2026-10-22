package com.fabio.GestionFacturas.application.ingreso.service;

import com.fabio.GestionFacturas.application.ingreso.port.in.CrearIngresoUseCase;
import com.fabio.GestionFacturas.application.ingreso.port.out.IngresoRepositoryPort;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementa {@link CrearIngresoUseCase}. Lo invoca {@code IngresoController} y habla solo con
 * {@link IngresoRepositoryPort}, nunca con el adapter JPA.
 */
@Service
public class CrearIngresoService implements CrearIngresoUseCase {

    private final IngresoRepositoryPort ingresoRepositoryPort;

    public CrearIngresoService(IngresoRepositoryPort ingresoRepositoryPort) {
        this.ingresoRepositoryPort = ingresoRepositoryPort;
    }

    @Override
    @Transactional
    public Ingreso crear(ComandoCrearIngreso comando) {
        String moneda = comando.moneda();
        if (moneda == null) {
            moneda = "EUR";
        }

        Dinero baseImponible = null;
        if (comando.baseImponible() != null) {
            baseImponible = new Dinero(comando.baseImponible(), moneda);
        }

        Dinero iva = null;
        if (comando.iva() != null) {
            iva = new Dinero(comando.iva(), moneda);
        }

        Dinero total = null;
        if (comando.total() != null) {
            total = new Dinero(comando.total(), moneda);
        }

        Ingreso ingreso = Ingreso.crear(comando.usuarioId(), comando.clienteId(), comando.concepto(),
                comando.fechaEmision(), baseImponible, iva, total);

        return ingresoRepositoryPort.guardar(ingreso);
    }
}
