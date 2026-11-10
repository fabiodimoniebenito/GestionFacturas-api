package com.fabio.GestionFacturas.infrastructure.adapter.in.web;

import com.fabio.GestionFacturas.domain.categoria.CategoriaNoEncontradaException;
import com.fabio.GestionFacturas.domain.cliente.ClienteDuplicadoException;
import com.fabio.GestionFacturas.domain.cliente.ClienteInvalidoException;
import com.fabio.GestionFacturas.domain.cliente.ClienteNoEncontradoException;
import com.fabio.GestionFacturas.domain.gasto.GastoEstadoInvalidoException;
import com.fabio.GestionFacturas.domain.gasto.GastoInvalidoException;
import com.fabio.GestionFacturas.domain.gasto.GastoNoEncontradoException;
import com.fabio.GestionFacturas.domain.ingreso.IngresoInvalidoException;
import com.fabio.GestionFacturas.domain.ingreso.IngresoNoEncontradoException;
import com.fabio.GestionFacturas.domain.usuario.CredencialesInvalidadException;
import com.fabio.GestionFacturas.domain.usuario.EmailYaRegistradoException;
import com.fabio.GestionFacturas.domain.gasto.ArchivoInvalidoException;
import com.fabio.GestionFacturas.infrastructure.adapter.out.ocr.OcrException;
import com.fabio.GestionFacturas.infrastructure.adapter.out.ocr.OcrNoDisponibleException;
import com.fabio.GestionFacturas.infrastructure.adapter.out.storage.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

/**
 * Traduce las excepciones de dominio y de validación a respuestas HTTP; es el único sitio que lo hace.
 * El dominio solo lanza excepciones normales y no conoce los códigos HTTP;
 * {@code @RestControllerAdvice} las intercepta en todos los controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GastoInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleGastoInvalido(GastoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({GastoNoEncontradoException.class, CategoriaNoEncontradaException.class})
    public ResponseEntity<Map<String, String>> handleGastoOCategoriaNoEncontrado(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));  // 404
    }

    @ExceptionHandler(GastoEstadoInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleGastoEstadoInvalido(GastoEstadoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));  // 409
    }

    @ExceptionHandler(IngresoInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleIngresoInvalido(IngresoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IngresoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleIngresoNoEncontrado(IngresoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));  // 404
    }

    @ExceptionHandler(ClienteInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleClienteInvalido(ClienteInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ClienteDuplicadoException.class)
    public ResponseEntity<Map<String, String>> handleClienteDuplicado(ClienteDuplicadoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);      // 409
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleClienteNoEncontrado(ClienteNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));  // 404
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<Map<String, String>> handleEmailDuplicado(EmailYaRegistradoException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);      // 409
    }


    @ExceptionHandler(CredencialesInvalidadException.class)
    public ResponseEntity<Map<String , String>> handleCredencialesInvalidas(CredencialesInvalidadException ex){
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);  // 401
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Map<String, String>> handleFileStorage(FileStorageException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleTamanoMaximoExcedido(MaxUploadSizeExceededException ex){
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)  // 413
                .body(Map.of("error", "El archivo supera el tamaño máximo permitido de 10 MB"));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> handleParteFaltante(MissingServletRequestPartException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Falta el archivo (parte 'archivo')"));
    }

    @ExceptionHandler(ArchivoInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleArchivoInvalido(ArchivoInvalidoException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    // Más específica que OcrException: Spring elige el handler de la clase más cercana
    @ExceptionHandler(OcrNoDisponibleException.class)
    public ResponseEntity<Map<String, String>> handleOcrNoDisponible(OcrNoDisponibleException ex){
        log.error("OCR no disponible (configuración/entorno): {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)  // 503
                .body(Map.of("error", "El servicio de lectura de imágenes no está disponible en este momento"));
    }

    @ExceptionHandler(OcrException.class)
    public ResponseEntity<Map<String, String>> handleOcr(OcrException ex){
        // Solo el detalle técnico; nunca el contenido del archivo
        log.warn("No se pudo leer la imagen con OCR: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)  // 422
                .body(Map.of("error", "No se pudo leer el contenido de la imagen"));
    }
}


