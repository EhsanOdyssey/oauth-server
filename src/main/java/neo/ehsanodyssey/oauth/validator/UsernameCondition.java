package neo.ehsanodyssey.oauth.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = UsernameConditionValidator.class)
@Documented
public @interface UsernameCondition {

    String message() default "{app.error.constraint.username}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
