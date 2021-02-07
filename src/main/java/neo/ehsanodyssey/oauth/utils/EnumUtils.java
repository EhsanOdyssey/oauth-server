package neo.ehsanodyssey.oauth.utils;

import org.springframework.util.StringUtils;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public class EnumUtils {

    public static <E extends Enum<E>> E valueOf(Class<E> enumType, String str) {
        if (!StringUtils.isEmpty(str)) {
            try {
                str = str.replace(" ", "_").toUpperCase();
                return Enum.valueOf(enumType, str);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}
