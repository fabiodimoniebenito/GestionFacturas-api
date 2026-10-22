package com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso;

import com.fabio.GestionFacturas.application.ingreso.port.in.ConsultarIngresosUseCase;
import com.fabio.GestionFacturas.application.ingreso.port.in.CrearIngresoUseCase;
import com.fabio.GestionFacturas.application.ingreso.port.in.RegistrarCobroUseCase;
import com.fabio.GestionFacturas.application.ingreso.port.in.RegistrarCobroUseCase.ComandoRegistrarCobro;
import com.fabio.GestionFacturas.application.ingreso.port.in.RevertirCobroUseCase;
import com.fabio.GestionFacturas.application.ingreso.port.in.RevertirCobroUseCase.ComandoRevertirCobro;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto.CrearIngresoRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto.IngresoResponse;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto.RegistrarCobroRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Adapter web (entrada) de facturas emitidas a clientes. Depende solo de los puertos de entrada
 * inferiores, nunca de los services ni repositorios que hay detrás. {@code IngresoWebMapper}
 * convierte entre DTOs y objetos de dominio, así un {@link Ingreso} nunca cruza directamente la
 * frontera HTTP. {@code usuarioId} siempre viene de {@code @AuthenticationPrincipal}, nunca del cuerpo de la petición.
 * <p>
 * Los endpoints de cobro delegan en {@link RegistrarCobroUseCase} y {@link RevertirCobroUseCase}.
 */
@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    private final CrearIngresoUseCase crearIngresoUseCase;
    private final ConsultarIngresosUseCase consultarIngresosUseCase;
    private final RegistrarCobroUseCase registrarCobroUseCase;
    private final RevertirCobroUseCase revertirCobroUseCase;

    public IngresoController(CrearIngresoUseCase crearIngresoUseCase,
                              ConsultarIngresosUseCase consultarIngresosUseCase,
                              RegistrarCobroUseCase registrarCobroUseCase,
                              RevertirCobroUseCase revertirCobroUseCase) {
        this.crearIngresoUseCase = crearIngresoUseCase;
        this.consultarIngresosUseCase = consultarIngresosUseCase;
        this.registrarCobroUseCase = registrarCobroUseCase;
        this.revertirCobroUseCase = revertirCobroUseCase;
    }

    @PostMapping
    public ResponseEntity<IngresoResponse> crear(@Valid @RequestBody CrearIngresoRequest request,
                                                  @AuthenticationPrincipal Long usuarioId) {
        Ingreso ingreso = crearIngresoUseCase.crear(IngresoWebMapper.aComando(request, usuarioId));
        return ResponseEntity.status(HttpStatus.CREATED).body(IngresoWebMapper.aRespuesta(ingreso));
    }

    @GetMapping
    public List<IngresoResponse> listar(@AuthenticationPrincipal Long usuarioId) {
        return consultarIngresosUseCase.listarPorUsuario(usuarioId)
                .stream()
                .map(IngresoWebMapper::aRespuesta)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngresoResponse> obtenerPorId(@PathVariable Long id,
                                                         @AuthenticationPrincipal Long usuarioId) {
        Optional<Ingreso> ingreso = consultarIngresosUseCase.obtenerPorId(id, usuarioId);
        if (ingreso.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(IngresoWebMapper.aRespuesta(ingreso.get()));
    }

    @PostMapping("/{id}/cobro")
    public ResponseEntity<IngresoResponse> registrarCobro(@PathVariable Long id,
                                                           @RequestBody RegistrarCobroRequest request,
                                                           @AuthenticationPrincipal Long usuarioId) {
        Ingreso ingreso = registrarCobroUseCase.registrarCobro(
                new ComandoRegistrarCobro(id, usuarioId, request.fechaCobro()));
        return ResponseEntity.ok(IngresoWebMapper.aRespuesta(ingreso));
    }

    @DeleteMapping("/{id}/cobro")
    public ResponseEntity<IngresoResponse> revertirCobro(@PathVariable Long id,
                                                          @AuthenticationPrincipal Long usuarioId) {
        Ingreso ingreso = revertirCobroUseCase.revertirCobro(new ComandoRevertirCobro(id, usuarioId));
        return ResponseEntity.ok(IngresoWebMapper.aRespuesta(ingreso));
    }
}
