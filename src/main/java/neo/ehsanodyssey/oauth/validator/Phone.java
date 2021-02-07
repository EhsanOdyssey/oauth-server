package neo.ehsanodyssey.oauth.validator;

import neo.ehsanodyssey.oauth.enums.PhoneType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface Phone {
    PhoneType type() default PhoneType.BOTH;

    String message() default "app.error.constraint.phone";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
