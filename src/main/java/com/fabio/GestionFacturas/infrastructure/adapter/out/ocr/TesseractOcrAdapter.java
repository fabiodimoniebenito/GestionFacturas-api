package com.fabio.GestionFacturas.infrastructure.adapter.out.ocr;

import com.fabio.GestionFacturas.application.shared.port.out.OcrPort;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Implementación real de {@link OcrPort} con Tess4J/Tesseract. Solo se activa con el perfil de Spring
 * "ocr" (el valor por defecto es {@code MockOcrAdapter}). Por ahora solo imágenes;
 * el soporte de PDF queda pendiente (PDFBox ya está disponible).
 */
@Component
@Profile("ocr")
public class TesseractOcrAdapter implements OcrPort {

    private final ITesseract tesseract;

    // datapath e idioma vienen de la configuración, nunca fijos en el código, para funcionar en cualquier máquina o despliegue
    public TesseractOcrAdapter(@Value("${app.ocr.tesseract.datapath:}") String datapath,
                               @Value("${app.ocr.tesseract.language:spa}") String language) {

        Tesseract instance = new Tesseract();
        if (datapath != null && !datapath.isBlank()){
            instance.setDatapath(datapath);
        }
        instance.setLanguage(language);
        this.tesseract = instance;
    }


    @Override
    public String extraerTexto(byte[] contenido) {
        BufferedImage imagen;
        try {
            imagen = ImageIO.read(new ByteArrayInputStream(contenido));
        } catch (IOException e) {
            throw new OcrException("No se pudo leer el archivo para aplicar OCR", e);
        }

        if (imagen == null) {
            throw new OcrException("Formato de archivo no soportado por OCR todavía (ej. PDF); solo imágenes por ahora");
        }

        // Nunca se escribe el texto reconocido en logs: es contenido de la factura del usuario
        try {
            return tesseract.doOCR(imagen);
        } catch (TesseractException e) {
            if (esProblemaDeConfiguracion(e)) {
                throw new OcrNoDisponibleException("Tesseract no está configurado correctamente (¿falta tessdata?): " + e.getMessage(), e);
            }
            throw new OcrException("Fallo al ejecutar OCR con Tesseract", e);
        } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
            throw new OcrNoDisponibleException("Librería nativa de Tesseract no disponible", e);
        }
    }

    private boolean esProblemaDeConfiguracion(TesseractException e) {
        String mensaje = e.getMessage();
        if (mensaje == null) {
            return false;
        }
        String minusculas = mensaje.toLowerCase();
        return minusculas.contains("data file")
                || minusculas.contains("tessdata")
                || minusculas.contains("traineddata")
                || minusculas.contains("failed loading language");
    }
}
