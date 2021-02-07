package neo.ehsanodyssey.oauth.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import neo.ehsanodyssey.oauth.config.handler.CustomOAuth2ExceptionSerializer;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 11 Dec 2019
 */
@JsonSerialize(using = CustomOAuth2ExceptionSerializer.class)
public class CustomOAuth2Exception extends OAuth2Exception {

    public CustomOAuth2Exception(String msg) {
        super(msg);
    }
}
