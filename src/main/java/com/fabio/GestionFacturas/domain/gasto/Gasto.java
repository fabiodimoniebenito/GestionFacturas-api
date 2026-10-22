package com.fabio.GestionFacturas.domain.gasto;


import com.fabio.GestionFacturas.domain.shared.Dinero;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de dominio: un gasto de un usuario, desde el borrador de OCR hasta el registro revisado.
 * POJO puro a propósito (arquitectura hexagonal): sin anotaciones de JPA/Spring;
 * el mapeo a persistencia vive en infraestructura (JpaEntity + Mapper).
 */
public class Gasto {

    private final Long id;
    private final Long usuarioId;
    private Long categoriaId;
    private Long clienteId;
    private String emisor;
    private LocalDate fechaEmision;
    private Dinero baseImponible;
    private Dinero iva;
    private Dinero total;
    private final String referenciaArchivo;
    private boolean deducible;
    private EstadoGasto estado;
    private final LocalDateTime creadoEn;

    /**
     * Constructor completo, usado por los mappers para reconstruir un Gasto desde persistencia.
     * Valida las invariantes del dominio por sí mismo, sin depender de ningún framework.
     */
    public Gasto(Long id, Long usuarioId, Long categoriaId, Long clienteId, String emisor,
                   LocalDate fechaEmision, Dinero baseImponible, Dinero iva, Dinero total,
                   String referenciaArchivo, boolean deducible, EstadoGasto estado, LocalDateTime creadoEn) {
        if (usuarioId == null) {
            throw new GastoInvalidoException("El gasto debe tener un usuario");
        }

        EstadoGasto estadoInicial = estado;
        if (estadoInicial == null) {
            estadoInicial = EstadoGasto.BORRADOR;
        }

        LocalDateTime fechaCreacion = creadoEn;
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }

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
        this.deducible = deducible;
        this.estado = estadoInicial;
        this.creadoEn = fechaCreacion;
    }

    /** Fábrica para un gasto introducido a mano: nace en BORRADOR, sin revisar. */
    public static Gasto crearBorrador(Long usuarioId, Long clienteId, String emisor, LocalDate fechaEmision,
                                        Dinero baseImponible, Dinero iva, Dinero total) {
        return new Gasto(null, usuarioId, null, clienteId, emisor, fechaEmision,
                baseImponible, iva, total, null, false, EstadoGasto.BORRADOR, LocalDateTime.now());
    }

    /** Fábrica para un gasto creado desde una factura digitalizada (flujo OCR); conserva la referencia al archivo. */
    public static Gasto crearBorradorDesdeArchivo(Long usuarioId, String emisor, LocalDate fechaEmision,
                                                    Dinero baseImponible, Dinero iva, Dinero total,
                                                    String referenciaArchivo) {
        return new Gasto(null, usuarioId, null, null, emisor, fechaEmision,
                baseImponible, iva, total, referenciaArchivo, false, EstadoGasto.BORRADOR, LocalDateTime.now());
    }

    /** El usuario confirma o corrige la categoría y la deducibilidad, y el gasto sale de BORRADOR. */
    public void revisar(Long categoriaId, boolean deducible) {
        validarRevisable();
        this.categoriaId = categoriaId;
        this.deducible = deducible;
        this.estado = EstadoGasto.REVISADA;
    }

    /** User confirms the data as it is: BORRADOR/PROCESADA -> REVISADA, only if fecha and total are present. */
    public void revisar() {
        validarRevisable();
        this.estado = EstadoGasto.REVISADA;
    }

    /**
     * Replaces the user-editable fields; estado is left untouched. Everything is validated before
     * anything is assigned, so a rejected edit never leaves the Gasto half-modified.
     */
    public void actualizar(String emisor, LocalDate fechaEmision, Dinero baseImponible, Dinero iva, Dinero total,
                           Long clienteId, Long categoriaId, boolean deducible) {
        if (estado == EstadoGasto.EXPORTADA) {
            throw new GastoEstadoInvalidoException("Un gasto exportado no se puede editar");
        }
        if (total == null) {
            throw new GastoInvalidoException("El total es obligatorio");
        }
        validarNoNegativo(baseImponible, "La base imponible");
        validarNoNegativo(iva, "El IVA");
        validarNoNegativo(total, "El total");

        this.emisor = emisor;
        this.fechaEmision = fechaEmision;
        this.baseImponible = baseImponible;
        this.iva = iva;
        this.total = total;
        this.clienteId = clienteId;
        this.categoriaId = categoriaId;
        this.deducible = deducible;
    }

    private void validarRevisable() {
        if (estado == EstadoGasto.REVISADA || estado == EstadoGasto.EXPORTADA) {
            throw new GastoEstadoInvalidoException("El gasto no se puede revisar en estado " + estado);
        }
        if (fechaEmision == null) {
            throw new GastoInvalidoException("El gasto necesita fecha de emisión para revisarse");
        }
        if (total == null) {
            throw new GastoInvalidoException("El gasto necesita total para revisarse");
        }
    }

    private void validarNoNegativo(Dinero dinero, String campo) {
        if (dinero != null && dinero.cantidad().signum() < 0) {
            throw new GastoInvalidoException(campo + " no puede ser negativo");
        }
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public Long getCategoriaId() { return categoriaId; }
    public Long getClienteId() { return clienteId; }
    public String getEmisor() { return emisor; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public Dinero getBaseImponible() { return baseImponible; }
    public Dinero getIva() { return iva; }
    public Dinero getTotal() { return total; }
    public String getReferenciaArchivo() { return referenciaArchivo; }
    public boolean isDeducible() { return deducible; }
    public EstadoGasto getEstado() { return estado; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}
