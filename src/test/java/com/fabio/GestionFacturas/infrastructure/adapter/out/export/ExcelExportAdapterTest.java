package com.fabio.GestionFacturas.infrastructure.adapter.out.export;

import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaGasto;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaIngreso;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaPendiente;
import com.fabio.GestionFacturas.application.shared.port.out.DatosExportacion.FilaRentabilidad;
import com.fabio.GestionFacturas.domain.dashboard.ResumenPeriodo;
import com.fabio.GestionFacturas.domain.gasto.EstadoGasto;
import com.fabio.GestionFacturas.domain.ingreso.EstadoCobro;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Escribe el libro y lo vuelve a leer con POI para comprobar tipos de celda, formatos y ausencia de fórmulas. */
class ExcelExportAdapterTest {

    private static final LocalDate DESDE = LocalDate.of(2026, 1, 1);
    private static final LocalDate HASTA = LocalDate.of(2026, 3, 31);

    private final ExcelExportAdapter adapter = new ExcelExportAdapter();

    private Dinero eur(String cantidad) {
        return Dinero.deEuros(new BigDecimal(cantidad));
    }

    private ResumenPeriodo resumen(String valor) {
        Dinero d = eur(valor);
        return new ResumenPeriodo(d, d, d, d, d, d, d, d, d, d);
    }

    private XSSFWorkbook leer(byte[] bytes) throws IOException {
        return new XSSFWorkbook(new ByteArrayInputStream(bytes));
    }

    private DatosExportacion vacio() {
        return new DatosExportacion(DESDE, HASTA, resumen("0.00"), List.of(), List.of(), List.of(),
                eur("0.00"), List.of());
    }

    @Test
    @DisplayName("el libro tiene las cinco hojas con sus cabeceras")
    void hojasYCabeceras() throws IOException {
        try (XSSFWorkbook libro = leer(adapter.generarExcel(vacio()))) {
            assertThat(libro.getNumberOfSheets()).isEqualTo(5);
            assertThat(libro.getSheetName(0)).isEqualTo("Resumen");
            assertThat(libro.getSheetName(1)).isEqualTo("Ingresos");
            assertThat(libro.getSheetName(2)).isEqualTo("Gastos");
            assertThat(libro.getSheetName(3)).isEqualTo("Pendientes de cobro");
            assertThat(libro.getSheetName(4)).isEqualTo("Rentabilidad por cliente");

            Sheet ingresos = libro.getSheet("Ingresos");
            assertThat(ingresos.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Fecha de emisión");
            assertThat(ingresos.getRow(0).getCell(7).getStringCellValue()).isEqualTo("Fecha de cobro");
            XSSFCellStyle estiloCabecera = (XSSFCellStyle) ingresos.getRow(0).getCell(0).getCellStyle();
            assertThat(estiloCabecera.getFont().getBold()).isTrue();
            assertThat(ingresos.getPaneInformation()).isNotNull();

            Sheet gastos = libro.getSheet("Gastos");
            assertThat(gastos.getRow(0).getCell(8).getStringCellValue()).isEqualTo("Deducible");
        }
    }

    @Test
    @DisplayName("periodo vacío: solo filas de cabecera en ingresos, gastos y rentabilidad")
    void periodoVacioSoloCabeceras() throws IOException {
        try (XSSFWorkbook libro = leer(adapter.generarExcel(vacio()))) {
            assertThat(libro.getSheet("Ingresos").getLastRowNum()).isEqualTo(0);
            assertThat(libro.getSheet("Gastos").getLastRowNum()).isEqualTo(0);
            assertThat(libro.getSheet("Rentabilidad por cliente").getLastRowNum()).isEqualTo(0);
            assertThat(libro.getSheet("Pendientes de cobro").getRow(0).getCell(0).getStringCellValue())
                    .isEqualTo("Fecha de emisión");
        }
    }

    @Test
    @DisplayName("importes numéricos con formato, fechas reales y textos como cadena sin fórmulas")
    void tiposDeCelda() throws IOException {
        List<FilaIngreso> ingresos = new ArrayList<>();
        ingresos.add(new FilaIngreso(LocalDate.of(2026, 2, 1), "=1+1", "@x", eur("100.00"), eur("21.00"),
                eur("121.00"), EstadoCobro.COBRADA, LocalDate.of(2026, 2, 10)));
        List<FilaGasto> gastos = new ArrayList<>();
        gastos.add(new FilaGasto(LocalDate.of(2026, 2, 2), "+cmd", null, null, eur("10.00"), eur("2.10"),
                eur("12.10"), EstadoGasto.REVISADA, true));
        DatosExportacion datos = new DatosExportacion(DESDE, HASTA, resumen("121.00"), ingresos, gastos,
                List.of(new FilaPendiente(LocalDate.of(2026, 2, 1), "Cliente", "Web", eur("121.00"))),
                eur("121.00"),
                List.of(new FilaRentabilidad("Gastos generales (sin cliente)", resumen("5.00"))));

        try (XSSFWorkbook libro = leer(adapter.generarExcel(datos))) {
            Sheet hoja = libro.getSheet("Ingresos");
            Cell total = hoja.getRow(1).getCell(5);
            assertThat(total.getCellType()).isEqualTo(CellType.NUMERIC);
            assertThat(total.getNumericCellValue()).isEqualTo(121.00);
            assertThat(total.getCellStyle().getDataFormatString()).isEqualTo("#,##0.00");

            Cell fecha = hoja.getRow(1).getCell(0);
            assertThat(fecha.getCellType()).isEqualTo(CellType.NUMERIC);
            assertThat(DateUtil.isCellDateFormatted(fecha)).isTrue();
            assertThat(fecha.getLocalDateTimeCellValue().toLocalDate()).isEqualTo(LocalDate.of(2026, 2, 1));
            assertThat(fecha.getCellStyle().getDataFormatString()).isEqualTo("dd/MM/yyyy");

            Cell inyeccion = hoja.getRow(1).getCell(1);
            assertThat(inyeccion.getCellType()).isEqualTo(CellType.STRING);
            assertThat(inyeccion.getStringCellValue()).isEqualTo("=1+1");
            assertThat(hoja.getRow(1).getCell(2).getCellType()).isEqualTo(CellType.STRING);

            assertThat(libro.getSheet("Gastos").getRow(1).getCell(1).getCellType()).isEqualTo(CellType.STRING);
            assertThat(libro.getSheet("Rentabilidad por cliente").getRow(1).getCell(0).getStringCellValue())
                    .isEqualTo("Gastos generales (sin cliente)");

            for (int s = 0; s < libro.getNumberOfSheets(); s++) {
                for (org.apache.poi.ss.usermodel.Row fila : libro.getSheetAt(s)) {
                    for (Cell celda : fila) {
                        assertThat(celda.getCellType()).isNotEqualTo(CellType.FORMULA);
                    }
                }
            }
        }
    }
}
