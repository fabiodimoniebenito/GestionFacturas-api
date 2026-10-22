package com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto;

import com.fabio.GestionFacturas.application.gasto.port.in.ConsultarGastosUseCase;
import com.fabio.GestionFacturas.application.gasto.port.in.CrearGastoUseCase;
import com.fabio.GestionFacturas.application.gasto.port.in.DigitalizarFacturaUseCase;
import com.fabio.GestionFacturas.application.gasto.port.in.DigitalizarFacturaUseCase.ComandoDigitalizarFactura;
import com.fabio.GestionFacturas.application.gasto.port.in.EditarGastoUseCase;
import com.fabio.GestionFacturas.application.gasto.port.in.RevisarGastoUseCase;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.CrearGastoRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.EditarGastoRequest;
import com.fabio.GestionFacturas.infrastructure.adapter.in.web.gasto.dto.GastoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Adapter web (entrada) de gastos. Depende solo de los tres puertos de entrada inferiores,
 * nunca de los services ni repositorios que hay detrás. {@code GastoWebMapper} convierte entre
 * DTOs y objetos de dominio, así un {@link Gasto} nunca cruza directamente la frontera HTTP.
 * {@code usuarioId} siempre viene de {@code @AuthenticationPrincipal}, que rellena {@code JwtAuthenticationFilter}
 * a partir del JWT validado; nunca se confía en el cuerpo ni en los parámetros de la petición.
 */
@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("application/pdf", "image/jpeg", "image/png", "image/webp");

    private final CrearGastoUseCase crearGastoUseCase;
    private final ConsultarGastosUseCase consultarGastosUseCase;
    private final DigitalizarFacturaUseCase digitalizarFacturaUseCase;
    private final EditarGastoUseCase editarGastoUseCase;
    private final RevisarGastoUseCase revisarGastoUseCase;

    public GastoController(CrearGastoUseCase crearGastoUseCase, ConsultarGastosUseCase consultarGastosUseCase,
                           DigitalizarFacturaUseCase digitalizarFacturaUseCase,
                           EditarGastoUseCase editarGastoUseCase, RevisarGastoUseCase revisarGastoUseCase) {
        this.crearGastoUseCase = crearGastoUseCase;
        this.consultarGastosUseCase = consultarGastosUseCase;
        this.digitalizarFacturaUseCase = digitalizarFacturaUseCase;
        this.editarGastoUseCase = editarGastoUseCase;
        this.revisarGastoUseCase = revisarGastoUseCase;
    }

    // DTO de petición -> comando -> CrearGastoUseCase (puerto) -> CrearGastoService -> Gasto de dominio -> DTO de respuesta
    @PostMapping
    public ResponseEntity<GastoResponse> crear(@Valid @RequestBody CrearGastoRequest request,
                                               @AuthenticationPrincipal Long usuarioId) {
        Gasto gasto = crearGastoUseCase.crear(GastoWebMapper.aComando(request, usuarioId));
        return ResponseEntity.status(HttpStatus.CREATED).body(GastoWebMapper.aRespuesta(gasto));
    }

    @GetMapping
    public List<GastoResponse> listar(@AuthenticationPrincipal Long usuarioId) {
        return consultarGastosUseCase.listarPorUsuario(usuarioId)
                .stream()
                .map(GastoWebMapper::aRespuesta)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GastoResponse> obtenerPorId(@PathVariable Long id,
                                                      @AuthenticationPrincipal Long usuarioId) {
        Optional<Gasto> gasto = consultarGastosUseCase.obtenerPorId(id, usuarioId);
        if (gasto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(GastoWebMapper.aRespuesta(gasto.get()));
    }

    @PutMapping("/{id}")
    public GastoResponse editar(@PathVariable Long id,
                                @Valid @RequestBody EditarGastoRequest request,
                                @AuthenticationPrincipal Long usuarioId) {
        Gasto gasto = editarGastoUseCase.editar(GastoWebMapper.aComando(request, id, usuarioId));
        return GastoWebMapper.aRespuesta(gasto);
    }

    @PostMapping("/{id}/revisar")
    public GastoResponse revisar(@PathVariable Long id, @AuthenticationPrincipal Long usuarioId) {
        Gasto gasto = revisarGastoUseCase.revisar(id, usuarioId);
        return GastoWebMapper.aRespuesta(gasto);
    }

    @PostMapping(path = "/digitalizar",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GastoResponse> digitalizar(@RequestParam("archivo") MultipartFile archivo,
                                                      @AuthenticationPrincipal Long usuarioId) {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        String contentType = archivo.getContentType();
        if (!TIPOS_PERMITIDOS.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de archivo no soportado: " + contentType);
        }

        // MultipartFile es un tipo del framework: se convierte a byte[] aquí mismo, porque los puertos
        // de aplicación no pueden depender de tipos de Spring MVC (ver el Javadoc de ComandoDigitalizarFactura).
        byte[] contenido;
        try {
            contenido = archivo.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo subido", e);
        }

        ComandoDigitalizarFactura comando = new ComandoDigitalizarFactura(
                usuarioId, contenido, archivo.getOriginalFilename(), contentType);

        // Lanza el pipeline OCR completo en DigitalizarFacturaService (almacenar -> OCR -> parsear -> guardar)
        Gasto gasto = digitalizarFacturaUseCase.digitalizar(comando);

        return ResponseEntity.status(HttpStatus.CREATED).body(GastoWebMapper.aRespuesta(gasto));
    }
}
