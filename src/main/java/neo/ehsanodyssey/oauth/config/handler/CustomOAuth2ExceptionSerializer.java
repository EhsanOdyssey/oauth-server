package neo.ehsanodyssey.oauth.config.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import neo.ehsanodyssey.oauth.exception.CustomOAuth2Exception;
import neo.ehsanodyssey.oauth.dto.ResponseModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public class CustomOAuth2ExceptionSerializer extends StdSerializer<CustomOAuth2Exception> {

    public CustomOAuth2ExceptionSerializer() {
        super(CustomOAuth2Exception.class);
    }

    @Override
    public void serialize(CustomOAuth2Exception value, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        List<Object> messages = Arrays.asList(value.getOAuth2ErrorCode(), value.getMessage());
        if (value.getAdditionalInformation() != null) {
            messages.add(value.getAdditionalInformation());
        }
        ResponseModel responseModel = new ResponseModel(false, messages);
        jsonGenerator.writeObject(responseModel);
    }
}