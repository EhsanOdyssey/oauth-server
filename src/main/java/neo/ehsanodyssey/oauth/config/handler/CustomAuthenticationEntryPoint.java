package neo.ehsanodyssey.oauth.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import neo.ehsanodyssey.oauth.dto.ResponseModel;
import neo.ehsanodyssey.oauth.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException authException) throws IOException, ServletException {
        ResponseModel responseModel;
        if (authException.getCause() instanceof ResourceNotFoundException) {
            responseModel = ((ResourceNotFoundException)authException.getCause()).getPayload();
        } else {
            List<String> messages = new ArrayList<>();
            messages.add("Unauthorised Access");
            if (StringUtils.hasText(authException.getMessage())) {
                messages.add(authException.getMessage());
            }
            responseModel = new ResponseModel(false, messages);
        }

        String json = new ObjectMapper().writeValueAsString(responseModel);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpServletResponse.getWriter().write(json);
        httpServletResponse.flushBuffer();
    }
}
