package neo.ehsanodyssey.oauth.validator;

import neo.ehsanodyssey.oauth.entity.User;
import neo.ehsanodyssey.oauth.enums.PhoneType;
import neo.ehsanodyssey.oauth.utils.PhoneUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public class PhoneValidator implements ConstraintValidator<Phone, Object> {

    @Autowired
    private MessageSource messageSource;

    private PhoneType type;
    private String message;

    @Override
    public void initialize(Phone annotation) {
        type = annotation.type();
        message = annotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        boolean validated = false;
        try {
            switch (type) {
                case PHONE_NUMBER:
                    validated = validateInPhoneNumberCase(value);
                    break;
                case STRING:
                    validated = validateInStringCase(value);
                    break;
                default:
                    validated = validateInBothCase(value);
            }
        } catch (ClassCastException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    messageSource.getMessage("app.error.constraint.phone.incompatible.message.template",
                            null,
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
        if (!validated) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    messageSource.getMessage(message,
                            null, LocaleContextHolder.getLocale())).addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateInStringCase(Object value) {
        return PhoneUtils.validateStrPhoneNumber((String) value);
    }

    private boolean validateInPhoneNumberCase(Object value) {
        return PhoneUtils.validateStrPhoneNumber(
                ((User.PhoneNumber) value).getValue(),
                StringUtils.hasText(((User.PhoneNumber) value).getRegionCode()) ?
                        ((User.PhoneNumber) value).getRegionCode() : PhoneUtils.DEFAULT_REGION_CODE);
    }

    private boolean validateInBothCase(Object value) {
        if (value instanceof User.PhoneNumber) {
            return validateInPhoneNumberCase(value);
        } else if (value instanceof String) {
            return validateInStringCase(value);
        }
        throw new ClassCastException();
    }
}
