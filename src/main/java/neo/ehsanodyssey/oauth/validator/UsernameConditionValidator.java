package neo.ehsanodyssey.oauth.validator;

import neo.ehsanodyssey.oauth.dto.UserModel;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public class UsernameConditionValidator implements ConstraintValidator<UsernameCondition, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (!(object instanceof UserModel)) {
            throw new IllegalArgumentException("@UsernameCondition only applies to UserModel");
        }
        UserModel userModel = (UserModel) object;
        return !StringUtils.isEmpty(userModel.getUsername()) || !StringUtils.isEmpty(userModel.getEmail()) ||
                !StringUtils.isEmpty(userModel.getPhoneNumber());
    }
}
