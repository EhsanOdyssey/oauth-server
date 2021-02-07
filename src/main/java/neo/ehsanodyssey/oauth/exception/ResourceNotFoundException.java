package neo.ehsanodyssey.oauth.exception;

import neo.ehsanodyssey.oauth.dto.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public final class ResourceNotFoundException extends RuntimeException {

    private final HttpStatus status;
    private final ResponseModel payload;

    public ResourceNotFoundException(HttpStatus status, ResponseModel payload) {
        this.status = status;
        this.payload = payload;
    }

    public ResponseModel getPayload() {
        return payload;
    }

    public ResponseEntity getResponseEntity() {
        return ResponseEntity.status(status).body(payload);
    }

}
