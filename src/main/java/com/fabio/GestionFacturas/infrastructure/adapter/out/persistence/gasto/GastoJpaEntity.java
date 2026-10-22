package com.fabio.GestionFacturas.infrastructure.adapter.out.persistence.gasto;

import com.fabio.GestionFacturas.domain.gasto.EstadoGasto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo de persistencia de Gasto: entidad JPA simple, separada del
 * {@link com.fabio.GestionFacturas.domain.gasto.Gasto} de dominio. {@code GastoMapper} convierte entre ambos;
 * esta clase nunca sale del adapter de persistencia. {@code @Enumerated(STRING)} guarda
 * {@code estado} como texto legible en la BD y no como ordinal, así los valores sobreviven a reordenar el enum.
 */
@Entity
@Table(name = "gasto")
public class GastoJpaEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(name = "cliente_id")
    private Long clienteId;

    private String emisor;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "base_imponible")
    private BigDecimal baseImponible;

    private BigDecimal iva;

    private BigDecimal total;

    @Column(name = "referencia_archivo")
    private String referenciaArchivo;

    // Una sola columna de moneda compartida por los tres importes Dinero (base/iva/total); ver GastoMapper
    @Column(nullable = false)
    private String moneda;

    @Column(nullable = false)
    private boolean deducible;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGasto estado;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    /** Constructor sin argumentos que JPA/Hibernate necesita para instanciar entidades por reflexión. */
    public GastoJpaEntity() {


    }

    public GastoJpaEntity(Long id, Long usuarioId, Long categoriaId, Long clienteId, String emisor, LocalDate fechaEmision,
                            BigDecimal baseImponible, BigDecimal iva, BigDecimal total, String referenciaArchivo,
                            String moneda, boolean deducible, EstadoGasto estado, LocalDateTime creadoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.categoriaId = categoriaId;
        this.clienteId = clienteId;
        this.emisor = emisor;
        this.fechaEmision = fechaEmision;
        this.baseImponible = baseImponible;
        this.iva = iva;
        this.total = total;
        this.referenciaArchivo = referenciaArchivo;
        this.moneda = moneda;
        this.deducible = deducible;
        this.estado = estado;
        this.creadoEn = creadoEn;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getEmisor() {
        return emisor;
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

    public String getReferenciaArchivo() {
        return referenciaArchivo;
    }

    public String getMoneda() {
        return moneda;
    }

    public boolean isDeducible() {
        return deducible;
    }

    public EstadoGasto getEstado() {
        return estado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }



}
