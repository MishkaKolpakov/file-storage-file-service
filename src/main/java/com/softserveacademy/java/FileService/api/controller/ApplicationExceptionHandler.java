package com.softserveacademy.java.FileService.api.controller;

import com.softserveacademy.java.FileService.exceptions.ErrorDTO;
import com.softserveacademy.java.FileService.exceptions.FileServiceException;
import com.softserveacademy.java.FileService.exceptions.PermissionException;
import com.softserveacademy.java.FileService.exceptions.StorageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * ApplicationExceptionHandler.class
 * Handles exceptions that can be thrown when application works
 *
 * @author Michael Yablon
 * @since 14.11.2017.
 */
@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler{

    /**
     * A fall-back handler that will catch all other exceptions
     * @param e - all other exceptions
     * @return response with internal server error code
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorDTO> handleAll(Exception e) {

        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), "Some unexpected error occurred");

        return new ResponseEntity<>(errorDTO, new HttpHeaders(), errorDTO.getStatus());
    }

    /**
     * Handles MissingServletRequestPartException
     * This exception is thrown when when the part of a multipart request not found
     *
     * @return response with error review
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST,
                ex.getLocalizedMessage(), "Required part of the request is missing");

        return new ResponseEntity<>(errorDTO, headers, errorDTO.getStatus());
    }

    /**
     * Handles MissingServletRequestParameterException
     * This exception is thrown when request missing parameter
     *
     * @return response with error review
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        String error = ex.getParameterName() + " parameter is missing";

        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);

        return new ResponseEntity<>(errorDTO, headers, errorDTO.getStatus());
    }

    /**
     * Handles MethodArgumentNotValidException
     * This exception is thrown when argument annotated with @Valid failed validation
     *
     * @return response with error review
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        List<String> errors = new ArrayList<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors())
            errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());

        for (ObjectError objectError : ex.getBindingResult().getGlobalErrors())
            errors.add(objectError.getObjectName() + ": " + objectError.getDefaultMessage());

        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.NOT_ACCEPTABLE, ex.getLocalizedMessage(), errors);

        return handleExceptionInternal(ex, errorDTO, headers, errorDTO.getStatus(), request);
    }

    /**
     * Handles ConstraintViolationException
     * This exception reports the result of constraint violations
     *
     * @return response with error review
     */
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ErrorDTO apiError = new ErrorDTO(HttpStatus.CONFLICT, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    /**
     * Handles HttpRequestMethodNotSupportedException
     * This exception is thrown when user send a request with an unsupported HTTP method
     *
     * @return response with error review
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t).append(" "));

        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage(), builder.toString());
        return new ResponseEntity<>(errorDTO, new HttpHeaders(), errorDTO.getStatus());
    }

    /**
     * Handles MethodArgumentTypeMismatchException
     * This exception is thrown when user send a request with type mismatch
     *
     * @return response with error review
     */
    @ExceptionHandler( MethodArgumentTypeMismatchException.class )
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        ErrorDTO errorDTO =
                new ErrorDTO(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(errorDTO, new HttpHeaders(), errorDTO.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ex.getLocalizedMessage(), builder.substring(0, builder.length() - 2));
        return new ResponseEntity<>(errorDTO, new HttpHeaders(), errorDTO.getStatus());
    }

    @ExceptionHandler(FileServiceException.class)
    public ResponseEntity<ErrorDTO> handleFileServiceException(FileServiceException ex) {

        ErrorDTO apiException = new ErrorDTO(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), "User not found");

        return new ResponseEntity<>(apiException, new HttpHeaders(), apiException.getStatus());
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<ErrorDTO> handlePermissionException(PermissionException ex) {

        ErrorDTO apiException = new ErrorDTO(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), "You have no permission for current operation");

        return new ResponseEntity<>(apiException, new HttpHeaders(), apiException.getStatus());
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorDTO> handleStorageException(StorageException ex) {

        ErrorDTO apiException = new ErrorDTO(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), "Storage issue");

        return new ResponseEntity<>(apiException, new HttpHeaders(), apiException.getStatus());
    }

}
