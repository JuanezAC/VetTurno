package com.devsenior.VetTurno.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones — Parte 6.
 * Traduce validaciones y reglas de negocio a 400, credenciales a 401,
 * email duplicado a 409 y fallos imprevistos a 500 con mensaje genérico.
 * Las respuestas 401/403 de seguridad NO pasan por aquí: las produce
 * la cadena de filtros de Spring Security antes de llegar al controller.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.put(fe.getField(), fe.getDefaultMessage());
        }
        ApiError cuerpo = new ApiError(HttpStatus.BAD_REQUEST.value(),
                "Los datos enviados no son válidos", errores);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ApiError> manejarNegocio(NegocioException ex) {
        ApiError cuerpo = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<ApiError> manejarEmailDuplicado(EmailYaRegistradoException ex) {
        ApiError cuerpo = new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> manejarAutenticacion(AuthenticationException ex) {
        ApiError cuerpo = new ApiError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cuerpo);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> manejarCuerpoNoLegible(HttpMessageNotReadableException ex) {
        ApiError cuerpo = new ApiError(HttpStatus.BAD_REQUEST.value(),
                "El cuerpo de la petición no tiene un formato válido", null);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> manejarTipoIncorrecto(MethodArgumentTypeMismatchException ex) {
        ApiError cuerpo = new ApiError(HttpStatus.BAD_REQUEST.value(),
                "Parámetro inválido: " + ex.getName(), null);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> manejarRecursoNoEncontrado(NoResourceFoundException ex) {
        ApiError cuerpo = new ApiError(HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> manejarTipoContenido(HttpMediaTypeNotSupportedException ex) {
        ApiError cuerpo = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "Content-Type no soportado: envíe application/json", null);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(cuerpo);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> manejarMetodoNoSoportado(HttpRequestMethodNotSupportedException ex) {
        ApiError cuerpo = new ApiError(HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Método HTTP no soportado para este recurso", null);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(cuerpo);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiError> manejarErrorHttp(ErrorResponseException ex) {
        int status = ex.getStatusCode().value();
        ApiError cuerpo = new ApiError(status, "La solicitud no pudo procesarse", null);
        return ResponseEntity.status(status).body(cuerpo);
    }

    @ExceptionHandler(RequestRejectedException.class)
    public ResponseEntity<ApiError> manejarSolicitudRechazada(RequestRejectedException ex) {
        ApiError cuerpo = new ApiError(HttpStatus.BAD_REQUEST.value(),
                "La solicitud fue rechazada: formato no válido", null);
        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> manejarGeneral(Exception ex) {
        ApiError cuerpo = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocurrió un error inesperado en el servidor", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cuerpo);
    }
}
