package com.fabio.GestionFacturas.infrastructure.config;

import com.fabio.GestionFacturas.domain.dashboard.CalculadoraPendientesCobro;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraRentabilidadClientes;
import com.fabio.GestionFacturas.domain.dashboard.CalculadoraResumenPeriodo;
import com.fabio.GestionFacturas.domain.gasto.FacturaTextParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra clases de dominio puro como beans de Spring para poder inyectarlas por constructor
 * (p. ej. en {@code DigitalizarFacturaService}) sin que el dominio dependa de Spring:
 * {@link FacturaTextParser} no lleva {@code @Component} y sigue siendo un POJO sin framework.
 */
@Configuration
public class DomainBeansConfig {

    @Bean
    public FacturaTextParser facturaTextParser() {
        return new FacturaTextParser();
    }

    @Bean
    public CalculadoraResumenPeriodo calculadoraResumenPeriodo() {
        return new CalculadoraResumenPeriodo();
    }

    @Bean
    public CalculadoraPendientesCobro calculadoraPendientesCobro() {
        return new CalculadoraPendientesCobro();
    }

    @Bean
    public CalculadoraRentabilidadClientes calculadoraRentabilidadClientes(
            CalculadoraResumenPeriodo calculadoraResumenPeriodo) {
        return new CalculadoraRentabilidadClientes(calculadoraResumenPeriodo);
    }
}
