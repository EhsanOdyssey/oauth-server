package neo.ehsanodyssey.oauth.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import neo.ehsanodyssey.oauth.dto.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        List<String> messages = new ArrayList<>();
        messages.add("Access Denied");
        if (StringUtils.hasText(accessDeniedException.getMessage())) {
            messages.add(accessDeniedException.getMessage());
        }
        ResponseModel responseModel = new ResponseModel(false, messages);
        String json = new ObjectMapper().writeValueAsString(responseModel);
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpServletResponse.getWriter().write(json);
        httpServletResponse.flushBuffer();
    }
}
