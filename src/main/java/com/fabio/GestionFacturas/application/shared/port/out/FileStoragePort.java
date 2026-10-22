package com.fabio.GestionFacturas.application.shared.port.out;

/**
 * Puerto de salida para guardar y recuperar archivos subidos. Hoy lo implementa
 * {@code LocalStorageAdapter} (sistema de archivos); está previsto un adapter con MinIO como reemplazo directo.
 * Quien lo usa solo ve una referencia opaca (String), nunca una ruta.
 */
public interface FileStoragePort {

    /** @return referencia opaca (no una ruta) que se guarda en el Gasto y sirve después para recuperar o borrar el archivo */
    String guardar(byte[] contenido, String nombreOriginal, String contentType);

    byte[] recuperar(String referencia);

    void eliminar(String referencia);

}
