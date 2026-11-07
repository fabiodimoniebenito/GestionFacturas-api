package com.fabio.GestionFacturas.infrastructure.adapter.in.web.exportacion;

import com.fabio.GestionFacturas.application.exportacion.port.in.ExportarPeriodoUseCase;
import com.fabio.GestionFacturas.application.exportacion.port.in.ExportarPeriodoUseCase.ComandoExportarPeriodo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/exportacion")
public class ExportacionController {

    static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ExportarPeriodoUseCase exportarPeriodo;

    public ExportacionController(ExportarPeriodoUseCase exportarPeriodo) {
        this.exportarPeriodo = exportarPeriodo;
    }

    @GetMapping(value = "/excel", produces = XLSX)
    @Operation(summary = "Exporta a Excel (.xlsx) los datos del periodo del usuario")
    @ApiResponse(responseCode = "200", description = "Fichero Excel",
            content = @Content(mediaType = XLSX, schema = @Schema(type = "string", format = "binary")))
    public ResponseEntity<byte[]> excel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @AuthenticationPrincipal Long usuarioId) {

        byte[] contenido = exportarPeriodo.exportar(new ComandoExportarPeriodo(usuarioId, desde, hasta));

        ContentDisposition disposicion = ContentDisposition.attachment()
                .filename("gestionfacturas_" + desde + "_" + hasta + ".xlsx")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(XLSX))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicion.toString())
                .body(contenido);
    }
}
