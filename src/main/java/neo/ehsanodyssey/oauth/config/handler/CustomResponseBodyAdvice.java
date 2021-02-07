package neo.ehsanodyssey.oauth.config.handler;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Date;
import java.util.Map;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 15 Dec 2019
 */
@ControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof DefaultOAuth2AccessToken) {
            ((DefaultOAuth2AccessToken) body).setExpiration(null);
        }
        if (body instanceof Map) {
            ((Map) body).remove(AccessTokenConverter.EXP);
            if (((Map) body).containsKey("refresh_token") && ((Map) body).get("refresh_token") instanceof DefaultExpiringOAuth2RefreshToken) {
                DefaultExpiringOAuth2RefreshToken refreshTokenObj = (DefaultExpiringOAuth2RefreshToken) ((Map) body).get("refresh_token");
                Date refreshExpiration = refreshTokenObj.getExpiration();
                String refreshToken = refreshTokenObj.getValue();
                ((Map) body).remove("refresh_token");
                ((Map) body).put("refresh_token", refreshToken);
                ((Map) body).put("refresh_token_eppires_at", refreshExpiration.getTime());
            }
        }
        return body;
    }
}
