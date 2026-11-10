package com.fabio.GestionFacturas.application.gasto.service;

import com.fabio.GestionFacturas.application.gasto.port.in.DigitalizarFacturaUseCase;
import com.fabio.GestionFacturas.application.gasto.port.out.GastoRepositoryPort;
import com.fabio.GestionFacturas.application.shared.port.out.FileStoragePort;
import com.fabio.GestionFacturas.application.shared.port.out.OcrPort;
import com.fabio.GestionFacturas.domain.gasto.DatosFacturaExtraidos;
import com.fabio.GestionFacturas.domain.gasto.FacturaTextParser;
import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.shared.Dinero;
import com.fabio.GestionFacturas.domain.gasto.ArchivoInvalidoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Implementa {@link DigitalizarFacturaUseCase} y orquesta la digitalización de una factura
 * combinando tres puertos de salida y un servicio de dominio, cada uno intercambiable:
 * <pre>
 *   controller -> este service -> FileStoragePort     (guarda el archivo;  prod: LocalStorageAdapter)
 *                              -> OcrPort             (imagen -> texto;    prod: TesseractOcrAdapter, dev/tests: MockOcrAdapter)
 *                              -> FacturaTextParser   (texto -> campos;    lógica de dominio pura)
 *                              -> GastoRepositoryPort (guarda el borrador; prod: GastoPersistenceAdapter)
 * </pre>
 * El OCR solo convierte píxeles en texto y nunca extrae campos; por eso la extracción
 * es un paso de dominio aparte y no forma parte de OcrPort.
 */
@Service
public class DigitalizarFacturaService implements DigitalizarFacturaUseCase {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png");

    private final FileStoragePort fileStoragePort;
    private final OcrPort ocrPort;
    private final FacturaTextParser facturaTextParser;
    private final GastoRepositoryPort gastoRepositoryPort;

    public DigitalizarFacturaService(FileStoragePort fileStoragePort,
                                     OcrPort ocrPort,
                                     FacturaTextParser facturaTextParser,
                                     GastoRepositoryPort gastoRepositoryPort) {
        this.fileStoragePort = fileStoragePort;
        this.ocrPort = ocrPort;
        this.facturaTextParser = facturaTextParser;
        this.gastoRepositoryPort = gastoRepositoryPort;
    }

    @Override
    @Transactional
    public Gasto digitalizar(ComandoDigitalizarFactura comando) {
        validarArchivo(comando);

        // Paso 1: guardar el archivo subido; en el Gasto se conserva la referencia, no los bytes
        String referencia = fileStoragePort.guardar(comando.contenido(), comando.nombreOriginal(), comando.contentType());

        // Si falla cualquier paso posterior, el archivo guardado quedaría huérfano (la transacción solo revierte la BD)
        try {
            return procesar(comando, referencia);
        } catch (RuntimeException e) {
            eliminarArchivoHuerfano(referencia, e);
            throw e;
        }
    }

    private void validarArchivo(ComandoDigitalizarFactura comando) {
        if (comando.contenido() == null || comando.contenido().length == 0) {
            throw new ArchivoInvalidoException("El archivo no puede estar vacío");
        }
        String contentType = comando.contentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType)) {
            if ("application/pdf".equals(contentType)) {
                throw new ArchivoInvalidoException(
                        "Los archivos PDF todavía no están soportados; sube una imagen JPG o PNG");
            }
            throw new ArchivoInvalidoException(
                    "Tipo de archivo no soportado: solo se admiten imágenes JPG o PNG");
        }
    }

    private void eliminarArchivoHuerfano(String referencia, RuntimeException causaOriginal) {
        try {
            fileStoragePort.eliminar(referencia);
        } catch (RuntimeException falloLimpieza) {
            // No debe tapar el error original; se deja constancia en la excepción original
            causaOriginal.addSuppressed(falloLimpieza);
        }
    }

    private Gasto procesar(ComandoDigitalizarFactura comando, String referencia) {
        // Paso 2: el adapter OCR convierte la imagen en texto (cuál se usa depende del perfil de Spring activo)
        String textoOcr = ocrPort.extraerTexto(comando.contenido());

        // Paso 3: el parser de dominio (sin framework) extrae los campos candidatos del texto
        DatosFacturaExtraidos datos = facturaTextParser.parsear(textoOcr);

        Dinero baseImponible = null;
        if (datos.baseImponible() != null) {
            baseImponible = Dinero.deEuros(datos.baseImponible());
        }

        Dinero iva = null;
        if (datos.iva() != null) {
            iva = Dinero.deEuros(datos.iva());
        }

        Dinero total = null;
        if (datos.total() != null) {
            total = Dinero.deEuros(datos.total());
        }

        // Paso 4: crear el Gasto en BORRADOR; el usuario corrige los campos después con revisar()
        Gasto gasto = Gasto.crearBorradorDesdeArchivo(comando.usuarioId(), datos.emisor(), datos.fechaEmision(),
                baseImponible, iva, total, referencia);

        return gastoRepositoryPort.guardar(gasto);
    }
}
