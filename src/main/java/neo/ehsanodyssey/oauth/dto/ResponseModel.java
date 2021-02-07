package neo.ehsanodyssey.oauth.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Getter
@Setter
public class ResponseModel implements Serializable {
    private Boolean status;
    private Object messages;
    private Object data;

    public ResponseModel(Boolean status, Object messages){
        this(status, messages, null);
    }

    public ResponseModel(Boolean status, Object messages, Object data){
        this.status = status;
        this.messages = Objects.requireNonNullElse(messages, "Success");
        this.data = data;
    }


}
