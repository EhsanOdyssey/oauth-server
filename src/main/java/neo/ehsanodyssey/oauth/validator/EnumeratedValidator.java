package neo.ehsanodyssey.oauth.validator;

import neo.ehsanodyssey.oauth.enums.EnumType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public class EnumeratedValidator implements ConstraintValidator<Enumerated, Object> {

    @Autowired
    private MessageSource messageSource;

    private List<String> acceptedValues;
    private Class<? extends Enum> eClass;
    private EnumType eType;
    private String message;
    private boolean hasParam;

    @Override
    public void initialize(Enumerated annotation) {
        eClass = annotation.eClass();
        acceptedValues = Stream.of(eClass
                .getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
        eType = annotation.eType();
        message = annotation.message();
        hasParam = annotation.hasParam();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null ||
                (value instanceof String && StringUtils.isEmpty(value)) ||
                (value instanceof Collection<?> && CollectionUtils.isEmpty((Collection<?>) value))) {
            return true;
        }
        Object[] unsuitableValues = null;
        try {
            switch (eType) {
                case STRING:
                    unsuitableValues = unsuitableValuesInStringCase(value);
                    break;
                case COLLECTION:
                    unsuitableValues = unsuitableValuesInCollectionCase(value);
                    break;
                default:
                    unsuitableValues = unsuitableValuesInEnumCase(value);
            }
        } catch (ClassCastException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    messageSource.getMessage("app.error.constraint.enumerated.incompatible.message.template",
                            new Object[]{StringUtils.capitalize(eType.name().toLowerCase()), eClass.getSimpleName()},
                            LocaleContextHolder.getLocale())
            ).addConstraintViolation();
            return false;
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    messageSource.getMessage("app.error.unexpected",
                            null, LocaleContextHolder.getLocale())).addConstraintViolation();
            return false;
        }
        if (unsuitableValues != null && unsuitableValues.length > 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    messageSource.getMessage(message, (hasParam ? new Object[] {Arrays.toString(unsuitableValues)} : null), LocaleContextHolder.getLocale())
            ).addConstraintViolation();
            return false;
        }
        return true;
    }

    private Object[] unsuitableValuesInEnumCase(Object value) {
        Enum e = (Enum) value;
        if (!acceptedValues.contains(e.name())) {
            return new Object[]{e.name()};
        }
        return new Object[]{};
    }

    private Object[] unsuitableValuesInStringCase(Object value) {
        String str = (String) value;
        return StringUtils.commaDelimitedListToSet(str.toUpperCase())
                .parallelStream().filter(s -> !acceptedValues.contains(s))
                .map(String::toLowerCase).toArray();
    }

    private Object[] unsuitableValuesInCollectionCase(Object value) {
        Collection<?> collection = (Collection<?>) value;
        Set<String> unsuitableValues = new HashSet<>();
        collection.parallelStream().forEach(element -> {
            if (element instanceof String) {
                if (!acceptedValues.contains(((String) element).toUpperCase())) {
                    unsuitableValues.add((String) element);
                }
            } else if (element instanceof Enum) {
                if (!acceptedValues.contains(((Enum) element).name())) {
                    unsuitableValues.add(((Enum) element).name());
                }
            }
        });
        return unsuitableValues.toArray();
    }
}
