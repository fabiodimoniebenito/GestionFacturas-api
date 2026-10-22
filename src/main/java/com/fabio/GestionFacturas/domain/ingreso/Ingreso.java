package com.fabio.GestionFacturas.domain.ingreso;

import com.fabio.GestionFacturas.domain.shared.Dinero;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de dominio: una factura emitida a un cliente, con seguimiento hasta que se cobra.
 * POJO puro a propósito (arquitectura hexagonal): sin anotaciones de JPA/Spring;
 * el mapeo a persistencia vive en infraestructura (JpaEntity + Mapper).
 */
public class Ingreso {

    private final Long id;
    private final Long usuarioId;
    private Long clienteId;
    private String concepto;
    private LocalDate fechaEmision;
    private Dinero baseImponible;
    private Dinero iva;
    private Dinero total;
    private EstadoCobro estadoCobro;
    private LocalDate fechaCobro;
    private final LocalDateTime creadoEn;

    /**
     * Constructor completo, usado por los mappers para reconstruir un Ingreso desde persistencia.
     * Valida las invariantes del dominio por sí mismo, sin depender de ningún framework.
     */
    public Ingreso(Long id, Long usuarioId, Long clienteId, String concepto, LocalDate fechaEmision,
                    Dinero baseImponible, Dinero iva, Dinero total, EstadoCobro estadoCobro,
                    LocalDate fechaCobro, LocalDateTime creadoEn) {
        if (usuarioId == null) {
            throw new IngresoInvalidoException("El ingreso debe tener un usuario");
        }

        EstadoCobro estadoInicial = estadoCobro;
        if (estadoInicial == null) {
            estadoInicial = EstadoCobro.PENDIENTE;
        }

        LocalDateTime fechaCreacion = creadoEn;
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }

        this.id = id;
        this.usuarioId = usuarioId;
        this.clienteId = clienteId;
        this.concepto = concepto;
        this.fechaEmision = fechaEmision;
        this.baseImponible = baseImponible;
        this.iva = iva;
        this.total = total;
        this.estadoCobro = estadoInicial;
        this.fechaCobro = fechaCobro;
        this.creadoEn = fechaCreacion;
    }

    /** Fábrica para una factura recién emitida: nace PENDIENTE, sin cobrar. */
    public static Ingreso crear(Long usuarioId, Long clienteId, String concepto, LocalDate fechaEmision,
                                 Dinero baseImponible, Dinero iva, Dinero total) {
        return new Ingreso(null, usuarioId, clienteId, concepto, fechaEmision,
                baseImponible, iva, total, EstadoCobro.PENDIENTE, null, LocalDateTime.now());
    }

    public void registrarCobro(LocalDate fechaCobro) {
        if (this.estadoCobro == EstadoCobro.COBRADA){
            throw new IngresoInvalidoException("El ingreso ya está cobrado");
        }
        if (fechaCobro.isBefore(this.fechaEmision)){
            throw new IngresoInvalidoException("La fecha de cobro no puede ser anterior a la fecha de emisión");
        }
        this.estadoCobro = EstadoCobro.COBRADA;
        this.fechaCobro = fechaCobro;
    }

    public void revertirCobro() {
        if (this.estadoCobro  == EstadoCobro.PENDIENTE){
            throw new IngresoInvalidoException("El ingreso no está cobrado, no hay cobro que revertir");
        }
        this.estadoCobro = EstadoCobro.PENDIENTE;
        this.fechaCobro = null;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public Long getClienteId() { return clienteId; }
    public String getConcepto() { return concepto; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public Dinero getBaseImponible() { return baseImponible; }
    public Dinero getIva() { return iva; }
    public Dinero getTotal() { return total; }
    public EstadoCobro getEstadoCobro() { return estadoCobro; }
    public LocalDate getFechaCobro() { return fechaCobro; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}
