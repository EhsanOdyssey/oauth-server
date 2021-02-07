package neo.ehsanodyssey.oauth.config.handler;

import neo.ehsanodyssey.oauth.dto.ResponseModel;
import neo.ehsanodyssey.oauth.exception.CustomParseException;
import neo.ehsanodyssey.oauth.exception.ResourceNotFoundException;
import neo.ehsanodyssey.oauth.exception.StorageException;
import neo.ehsanodyssey.oauth.utils.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    public RestResponseEntityExceptionHandler() {
        super();
    }

    // API

    // 400

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleBadRequest(final ConstraintViolationException ex, final WebRequest request) {
        return handleExceptionInternal(ex,
                new ResponseModel(false, ExceptionUtils.extractConstraintViolationException(ex)),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
        return handleExceptionInternal(ex,
                new ResponseModel(false, ExceptionUtils.extractDataIntegrityViolationException(ex)),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return handleExceptionInternal(ex,
                new ResponseModel(false, ExceptionUtils.extractMapFromException(ex, messageSource, LocaleContextHolder.getLocale())),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        return handleExceptionInternal(ex,
                new ResponseModel(false, ExceptionUtils.extractMapFromException(ex, messageSource, LocaleContextHolder.getLocale())),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }


    // 404

    @ExceptionHandler(value = { ResourceNotFoundException.class })
    protected ResponseEntity<Object> handleNotFound(final ResourceNotFoundException ex, final WebRequest request) {
        return handleExceptionInternal(ex, ex.getResponseEntity(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { StorageException.class})
    protected ResponseEntity<Object> handleUserMistake(final StorageException ex, final WebRequest request) {
        return handleExceptionInternal(ex, new ResponseModel(false, ex.getReason()), new HttpHeaders(), ex.getStatus(), request);
    }

    // 409

    @ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class })
    protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
        final String bodyOfResponse = "error in data access layer";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    // 412

    // 500

    @ExceptionHandler({ CustomParseException.class, NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        logger.error("500 Status Code", ex);
        return handleExceptionInternal(ex,
                new ResponseModel(false,
                        ExceptionUtils.extractMessageFromRuntimeException(ex, messageSource, LocaleContextHolder.getLocale())),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}