package neo.ehsanodyssey.oauth.utils;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import neo.ehsanodyssey.oauth.exception.CustomParseException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public class ExceptionUtils {

    public static final String APP_ERROR_PARSE_ATTRIBUTE = "app.error.parse.attribute";

    private ExceptionUtils() {
    }

    public static Map<String, String> extractErrorMessagesFromBindingResult(BindingResult result) {
        Map<String, String> errorMessages = new HashMap<>();
        if (result.hasErrors()) {
            for (Object object : result.getAllErrors()) {
                if (object instanceof FieldError) {
                    FieldError fieldError = (FieldError) object;
                    errorMessages.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
                } else if (object instanceof ObjectError) {
                    ObjectError fieldError = (ObjectError) object;
                    errorMessages.putIfAbsent(fieldError.getCode(), fieldError.getDefaultMessage());
                }
            }
        }
        return errorMessages;
    }

    public static Map<String, String> extractConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        if (!violations.isEmpty()) {
            violations.forEach(v -> {
                if (v.getPropertyPath() != null) {
                    errors.putIfAbsent(v.getPropertyPath().toString(), v.getMessage());
                }
            });
        }
        return errors;
    }

    public static Map<String, String> extractDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        return errors;
    }

    public static Map<String, String> extractMapFromException(Exception ex, MessageSource messageSource,
                                                              Locale locale) {
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException) {
            return extractErrorMessagesFromBindingResult(((MethodArgumentNotValidException)ex).getBindingResult());
        } else if (ex.getCause() instanceof MismatchedInputException) {
            ((MismatchedInputException)ex.getCause()).getPath().parallelStream()
                    .filter(reference -> StringUtils.hasText(reference.getFieldName()))
                    .forEach(reference -> errors.putIfAbsent(reference.getFieldName(), messageSource.getMessage("app.error.mismatched.input.value", null, locale)));
        } else if (ex.getCause() instanceof JsonMappingException) {
            ((JsonMappingException)ex.getCause()).getPath().parallelStream()
                    .filter(reference -> StringUtils.hasText(reference.getFieldName()))
                    .forEach(reference -> errors.putIfAbsent(reference.getFieldName(), messageSource.getMessage("app.error.json.parse.input.value", null, locale)));
        }
        return errors;
    }

    public static String extractMessageFromRuntimeException(RuntimeException ex, MessageSource messageSource,
                                                                         Locale locale) {
        if (ex instanceof CustomParseException) {
            if (StringUtils.hasText(ex.getMessage()) && ex.getMessage().startsWith("app.error.")) {
                return messageSource.getMessage(String.format(messageSource.getMessage(ex.getMessage(), null, locale), ((CustomParseException)ex).getFiled()), null, locale);
            }
        }
        if (StringUtils.hasText(ex.getMessage()) && ex.getMessage().startsWith("app.error.")) {
            return messageSource.getMessage(ex.getMessage(), null, locale);
        }
        return StringUtils.isEmpty(ex.getMessage()) ?
                messageSource.getMessage("app.error.unexpected", null, locale) : ex.getMessage();
    }
}
