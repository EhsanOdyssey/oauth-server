package neo.ehsanodyssey.oauth.validator;

import neo.ehsanodyssey.oauth.enums.EnumType;

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
@Constraint(validatedBy = EnumeratedValidator.class)
@Documented
public @interface Enumerated {

    Class<? extends Enum> eClass();

    EnumType eType() default EnumType.ENUM;

    String message() default "app.error.constraint.enumerated.unsuitable.values";

    boolean hasParam() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
