package com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente;

import com.fabio.GestionFacturas.application.cliente.port.in.ConsultarClientesUseCase;
import com.fabio.GestionFacturas.application.cliente.port.in.CrearClienteUseCase;
import com.fabio.GestionFacturas.application.cliente.port.in.DesactivarClienteUseCase;
import com.fabio.GestionFacturas.application.cliente.port.in.DesactivarClienteUseCase.ComandoDesactivarCliente;
import com.fabio.GestionFacturas.application.cliente.port.in.EditarClienteUseCase;
import com.fabio.GestionFacturas.domain.cliente.Cliente;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto.ClienteResponse;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto.CrearClienteRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.cliente.dto.EditarClienteRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Adapter web (entrada) de clientes. Depende solo de los cuatro puertos de entrada inferiores,
 * nunca de los services ni repositorios que hay detrás. {@code ClienteWebMapper} convierte entre
 * DTOs y objetos de dominio, así un {@link Cliente} nunca cruza directamente la frontera HTTP.
 * {@code usuarioId} siempre viene de {@code @AuthenticationPrincipal}, que rellena
 * {@code JwtAuthenticationFilter} a partir del JWT validado; nunca se confía en la petición.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final CrearClienteUseCase crearClienteUseCase;
    private final ConsultarClientesUseCase consultarClientesUseCase;
    private final EditarClienteUseCase editarClienteUseCase;
    private final DesactivarClienteUseCase desactivarClienteUseCase;

    public ClienteController(CrearClienteUseCase crearClienteUseCase,
                              ConsultarClientesUseCase consultarClientesUseCase,
                              EditarClienteUseCase editarClienteUseCase,
                              DesactivarClienteUseCase desactivarClienteUseCase) {
        this.crearClienteUseCase = crearClienteUseCase;
        this.consultarClientesUseCase = consultarClientesUseCase;
        this.editarClienteUseCase = editarClienteUseCase;
        this.desactivarClienteUseCase = desactivarClienteUseCase;
    }

    // DTO de petición -> comando -> CrearClienteUseCase (puerto) -> CrearClienteService -> Cliente de dominio -> DTO de respuesta
    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody CrearClienteRequest request,
                                                  @AuthenticationPrincipal Long usuarioId) {
        Cliente cliente = crearClienteUseCase.crear(ClienteWebMapper.aComandoCrear(request, usuarioId));
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteWebMapper.aRespuesta(cliente));
    }

    @GetMapping
    public List<ClienteResponse> listar(@AuthenticationPrincipal Long usuarioId) {
        return consultarClientesUseCase.listarPorUsuario(usuarioId)
                .stream()
                .map(ClienteWebMapper::aRespuesta)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long id,
                                                         @AuthenticationPrincipal Long usuarioId) {
        Optional<Cliente> cliente = consultarClientesUseCase.obtenerPorId(id, usuarioId);
        if (cliente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ClienteWebMapper.aRespuesta(cliente.get()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> editar(@PathVariable Long id,
                                                   @Valid @RequestBody EditarClienteRequest request,
                                                   @AuthenticationPrincipal Long usuarioId) {
        Cliente cliente = editarClienteUseCase.editar(ClienteWebMapper.aComandoEditar(id, request, usuarioId));
        return ResponseEntity.ok(ClienteWebMapper.aRespuesta(cliente));
    }

    // Borrado lógico: DesactivarClienteUseCase pone activo=false y conserva la fila
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id, @AuthenticationPrincipal Long usuarioId) {
        desactivarClienteUseCase.desactivar(new ComandoDesactivarCliente(id, usuarioId));
        return ResponseEntity.noContent().build();
    }
}
