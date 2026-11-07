package com.fabio.GestionFacturas.application.shared.port.out;

/**
 * Puerto de salida para generar el fichero de exportación de un periodo. Recibe un objeto de datos plano
 * ({@link DatosExportacion}) y devuelve los bytes del fichero; el formato concreto (xlsx) vive en el adapter.
 */
public interface ExportPort {

    byte[] generarExcel(DatosExportacion datos);
}
