package com.fabio.GestionFacturas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada. {@code @SpringBootApplication} activa el escaneo de componentes, la autoconfiguración y el cableado de beans. */
@SpringBootApplication
public class GestionFacturasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionFacturasApplication.class, args);
	}

}
