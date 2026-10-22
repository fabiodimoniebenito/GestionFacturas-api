package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.ingreso;

import com.fabio.GestionFacturas.domain.ingreso.EstadoCobro;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo de persistencia de Ingreso: entidad JPA simple, separada del
 * {@link com.fabio.GestionFacturas.domain.ingreso.Ingreso} de dominio. {@code IngresoMapper} convierte entre ambos;
 * esta clase nunca sale del adapter de persistencia. {@code @Enumerated(STRING)} guarda
 * {@code estadoCobro} como texto legible en la BD y no como ordinal, así los valores sobreviven a reordenar el enum.
 */
@Entity
@Table(name = "ingreso")
public class IngresoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "cliente_id")
    private Long clienteId;

    private String concepto;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "base_imponible")
    private BigDecimal baseImponible;

    private BigDecimal iva;

    private BigDecimal total;

    // Una sola columna de moneda compartida por los tres importes Dinero (base/iva/total); ver IngresoMapper
    @Column(nullable = false)
    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cobro", nullable = false)
    private EstadoCobro estadoCobro;

    @Column(name = "fecha_cobro")
    private LocalDate fechaCobro;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    /** Constructor sin argumentos que JPA/Hibernate necesita para instanciar entidades por reflexión. */
    public IngresoJpaEntity() {
    }

    public IngresoJpaEntity(Long id, Long usuarioId, Long clienteId, String concepto, LocalDate fechaEmision,
                             BigDecimal baseImponible, BigDecimal iva, BigDecimal total, String moneda,
                             EstadoCobro estadoCobro, LocalDate fechaCobro, LocalDateTime creadoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.clienteId = clienteId;
        this.concepto = concepto;
        this.fechaEmision = fechaEmision;
        this.baseImponible = baseImponible;
        this.iva = iva;
        this.total = total;
        this.moneda = moneda;
        this.estadoCobro = estadoCobro;
        this.fechaCobro = fechaCobro;
        this.creadoEn = creadoEn;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getConcepto() {
        return concepto;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public BigDecimal getBaseImponible() {
        return baseImponible;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getMoneda() {
        return moneda;
    }

    public EstadoCobro getEstadoCobro() {
        return estadoCobro;
    }

    public LocalDate getFechaCobro() {
        return fechaCobro;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
