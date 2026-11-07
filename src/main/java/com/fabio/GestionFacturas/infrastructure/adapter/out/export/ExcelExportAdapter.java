package com.fabio.GestionFacturas.infrastructure.adapter.out.export;

import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaGasto;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaIngreso;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaPendiente;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaRentabilidad;
import com.fabio.GestionFacturas.application.shared.port.out.ExportPort;
import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;

/**
 * Adapter de {@link ExportPort} que genera un .xlsx con Apache POI (la librería vive solo aquí).
 * Importes: celdas numéricas con formato #,##0.00. Fechas: celdas de fecha dd/MM/yyyy. Texto: SIEMPRE
 * celdas de cadena (nunca fórmulas), así que valores como "=1+1" o "@x" quedan inertes.
 */
@Component
public class ExcelExportAdapter implements ExportPort {

    private static final String FORMATO_DINERO = "#,##0.00";
    private static final String FORMATO_FECHA = "dd/MM/yyyy";
    private static final int ANCHO_COLUMNA = 20 * 256;

    @Override
    public byte[] generarExcel(DatosExportacion datos) {
        try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Estilos estilos = new Estilos(libro);

            hojaResumen(libro, estilos, datos);
            hojaIngresos(libro, estilos, datos);
            hojaGastos(libro, estilos, datos);
            hojaPendientes(libro, estilos, datos);
            hojaRentabilidad(libro, estilos, datos);

            libro.write(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el fichero Excel", e);
        }
    }

    private void hojaResumen(XSSFWorkbook libro, Estilos estilos, DatosExportacion datos) {
        Sheet hoja = libro.createSheet("Resumen");
        cabecera(hoja, estilos, "Concepto", "Total", "Base");

        int fila = 1;
        Row periodo = hoja.createRow(fila++);
        texto(periodo, 0, "Periodo desde");
        fecha(periodo, 1, datos.desde(), estilos);
        Row periodoHasta = hoja.createRow(fila++);
        texto(periodoHasta, 0, "Periodo hasta");
        fecha(periodoHasta, 1, datos.hasta(), estilos);

        ResumenPeriodo r = datos.resumen();
        fila = filaCifras(hoja, estilos, fila, "Facturado", r.facturadoTotal(), r.facturadoBase());
        fila = filaCifras(hoja, estilos, fila, "Cobrado", r.cobradoTotal(), r.cobradoBase());
        fila = filaCifras(hoja, estilos, fila, "Gastos", r.gastosTotal(), r.gastosBase());
        fila = filaCifras(hoja, estilos, fila, "Beneficio de caja (cobrado - gastos)",
                r.beneficioCajaTotal(), r.beneficioCajaBase());
        filaCifras(hoja, estilos, fila, "Beneficio facturado (facturado - gastos)",
                r.beneficioFacturadoTotal(), r.beneficioFacturadoBase());

        anchos(hoja, 3);
        hoja.setColumnWidth(0, 42 * 256);
    }

    private int filaCifras(Sheet hoja, Estilos estilos, int fila, String etiqueta, Dinero total, Dinero base) {
        Row row = hoja.createRow(fila);
        texto(row, 0, etiqueta);
        dinero(row, 1, total, estilos);
        dinero(row, 2, base, estilos);
        return fila + 1;
    }

    private void hojaIngresos(XSSFWorkbook libro, Estilos estilos, DatosExportacion datos) {
        Sheet hoja = libro.createSheet("Ingresos");
        cabecera(hoja, estilos, "Fecha de emisión", "Cliente", "Concepto", "Base", "IVA", "Total",
                "Estado de cobro", "Fecha de cobro");
        int fila = 1;
        for (FilaIngreso i : datos.ingresos()) {
            Row row = hoja.createRow(fila++);
            fecha(row, 0, i.fechaEmision(), estilos);
            texto(row, 1, i.clienteNombre());
            texto(row, 2, i.concepto());
            dinero(row, 3, i.base(), estilos);
            dinero(row, 4, i.iva(), estilos);
            dinero(row, 5, i.total(), estilos);
            if (i.estadoCobro() != null) {
                texto(row, 6, i.estadoCobro().name());
            }
            fecha(row, 7, i.fechaCobro(), estilos);
        }
        anchos(hoja, 8);
    }

    private void hojaGastos(XSSFWorkbook libro, Estilos estilos, DatosExportacion datos) {
        Sheet hoja = libro.createSheet("Gastos");
        cabecera(hoja, estilos, "Fecha", "Emisor", "Cliente", "Categoría", "Base", "IVA", "Total",
                "Estado", "Deducible");
        int fila = 1;
        for (FilaGasto g : datos.gastos()) {
            Row row = hoja.createRow(fila++);
            fecha(row, 0, g.fecha(), estilos);
            texto(row, 1, g.emisor());
            texto(row, 2, g.clienteNombre());
            texto(row, 3, g.categoriaNombre());
            dinero(row, 4, g.base(), estilos);
            dinero(row, 5, g.iva(), estilos);
            dinero(row, 6, g.total(), estilos);
            if (g.estado() != null) {
                texto(row, 7, g.estado().name());
            }
            if (g.deducible()) {
                texto(row, 8, "Sí");
            } else {
                texto(row, 8, "No");
            }
        }
        anchos(hoja, 9);
    }

    private void hojaPendientes(XSSFWorkbook libro, Estilos estilos, DatosExportacion datos) {
        Sheet hoja = libro.createSheet("Pendientes de cobro");
        cabecera(hoja, estilos, "Fecha de emisión", "Cliente", "Concepto", "Total");
        int fila = 1;
        for (FilaPendiente p : datos.pendientes()) {
            Row row = hoja.createRow(fila++);
            fecha(row, 0, p.fechaEmision(), estilos);
            texto(row, 1, p.clienteNombre());
            texto(row, 2, p.concepto());
            dinero(row, 3, p.total(), estilos);
        }
        Row total = hoja.createRow(fila);
        texto(total, 2, "Total pendiente");
        dinero(total, 3, datos.totalPendiente(), estilos);
        anchos(hoja, 4);
    }

    private void hojaRentabilidad(XSSFWorkbook libro, Estilos estilos, DatosExportacion datos) {
        Sheet hoja = libro.createSheet("Rentabilidad por cliente");
        cabecera(hoja, estilos, "Cliente",
                "Facturado total", "Facturado base", "Cobrado total", "Cobrado base",
                "Gastos total", "Gastos base",
                "Beneficio caja total", "Beneficio caja base",
                "Beneficio facturado total", "Beneficio facturado base");
        int fila = 1;
        for (FilaRentabilidad f : datos.rentabilidad()) {
            Row row = hoja.createRow(fila++);
            ResumenPeriodo r = f.resumen();
            texto(row, 0, f.nombre());
            dinero(row, 1, r.facturadoTotal(), estilos);
            dinero(row, 2, r.facturadoBase(), estilos);
            dinero(row, 3, r.cobradoTotal(), estilos);
            dinero(row, 4, r.cobradoBase(), estilos);
            dinero(row, 5, r.gastosTotal(), estilos);
            dinero(row, 6, r.gastosBase(), estilos);
            dinero(row, 7, r.beneficioCajaTotal(), estilos);
            dinero(row, 8, r.beneficioCajaBase(), estilos);
            dinero(row, 9, r.beneficioFacturadoTotal(), estilos);
            dinero(row, 10, r.beneficioFacturadoBase(), estilos);
        }
        anchos(hoja, 11);
        hoja.setColumnWidth(0, 32 * 256);
    }

    private void cabecera(Sheet hoja, Estilos estilos, String... titulos) {
        Row row = hoja.createRow(0);
        for (int i = 0; i < titulos.length; i++) {
            Cell celda = row.createCell(i);
            celda.setCellValue(titulos[i]);
            celda.setCellStyle(estilos.cabecera);
        }
        hoja.createFreezePane(0, 1);
    }

    private void anchos(Sheet hoja, int columnas) {
        for (int i = 0; i < columnas; i++) {
            hoja.setColumnWidth(i, ANCHO_COLUMNA);
        }
    }

    /** Siempre celda de cadena: nunca se interpreta como fórmula. Null deja la celda vacía. */
    private void texto(Row row, int columna, String valor) {
        if (valor == null) {
            return;
        }
        Cell celda = row.createCell(columna);
        celda.setCellValue(valor);
    }

    private void dinero(Row row, int columna, Dinero valor, Estilos estilos) {
        if (valor == null) {
            return;
        }
        Cell celda = row.createCell(columna);
        celda.setCellValue(valor.cantidad().doubleValue());
        celda.setCellStyle(estilos.dinero);
    }

    private void fecha(Row row, int columna, LocalDate valor, Estilos estilos) {
        if (valor == null) {
            return;
        }
        Cell celda = row.createCell(columna);
        celda.setCellValue(valor);
        celda.setCellStyle(estilos.fecha);
    }

    private static final class Estilos {
        final CellStyle cabecera;
        final CellStyle dinero;
        final CellStyle fecha;

        Estilos(XSSFWorkbook libro) {
            CreationHelper helper = libro.getCreationHelper();

            Font negrita = libro.createFont();
            negrita.setBold(true);
            cabecera = libro.createCellStyle();
            cabecera.setFont(negrita);

            dinero = libro.createCellStyle();
            dinero.setDataFormat(helper.createDataFormat().getFormat(FORMATO_DINERO));

            fecha = libro.createCellStyle();
            fecha.setDataFormat(helper.createDataFormat().getFormat(FORMATO_FECHA));
        }
    }
}
