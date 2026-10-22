package com.fabio.GestionFacturas.infrastructure.adapter.in.web.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Comprobación pública de disponibilidad (ruta pública en SecurityConfig); sin puerto ni service, solo una respuesta fija. */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public HealthResponse estado() {
        return new HealthResponse("UP");
    }

    public record HealthResponse(String status) {}
}
