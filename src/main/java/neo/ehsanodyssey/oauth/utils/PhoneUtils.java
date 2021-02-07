package neo.ehsanodyssey.oauth.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import neo.ehsanodyssey.oauth.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public class PhoneUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(PhoneUtils.class);
    private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();
    public static final String DEFAULT_REGION_CODE = "IR";

    public static boolean validatePhoneNumberDto(User.PhoneNumber phoneNumber) {
        return validateStrPhoneNumber(phoneNumber.getValue(), phoneNumber.getRegionCode());
    }

    public static boolean validateStrPhoneNumber(String phoneNumber) {
        return validateStrPhoneNumber(phoneNumber, null);
    }

    public static boolean validateStrPhoneNumber(String phoneNumber, String regionCode) {
        try {
            Phonenumber.PhoneNumber phone = PHONE_NUMBER_UTIL.parse(phoneNumber, StringUtils.hasText(regionCode) ? regionCode : DEFAULT_REGION_CODE);
            return PHONE_NUMBER_UTIL.isValidNumber(phone);
        } catch (NumberParseException e) {
            LOGGER.error("PhoneNumber parse exception.", e);
        }
        return false;
    }

    public static String getFormattedPhoneNumber(String phoneNumber, PhoneNumberUtil.PhoneNumberFormat format) {
        return getFormattedPhoneNumber(phoneNumber, DEFAULT_REGION_CODE, format);
    }

    public static String getFormattedPhoneNumber(String phoneNumber, String defaultRegionCode, PhoneNumberUtil.PhoneNumberFormat format) {
        try {
            Phonenumber.PhoneNumber phone = PHONE_NUMBER_UTIL.parse(phoneNumber, defaultRegionCode);
            if(PHONE_NUMBER_UTIL.isValidNumber(phone)) {
                return PHONE_NUMBER_UTIL.format(phone, format);
            }
        } catch (NumberParseException e) {
            LOGGER.error("PhoneNumber parse exception.", e);
        }
        return null;
    }

    public static User.PhoneNumber getPhoneNumber(String phoneNumber) {
        return getPhoneNumber(phoneNumber, null);
    }

    public static User.PhoneNumber getPhoneNumber(String phoneNumber, String regionCode) {
        try {
            Phonenumber.PhoneNumber phone = PHONE_NUMBER_UTIL.parse(phoneNumber, StringUtils.hasText(regionCode) ? regionCode : DEFAULT_REGION_CODE);
            if(PHONE_NUMBER_UTIL.isValidNumber(phone)) {
                return new User.PhoneNumber(
                        String.valueOf(phone.getNationalNumber()),
                        PHONE_NUMBER_UTIL.getRegionCodeForNumber(phone)
                );
            }
        } catch (NumberParseException e) {
            LOGGER.error("PhoneNumber parse exception.", e);
        }
        return null;
    }
}
