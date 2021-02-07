package neo.ehsanodyssey.oauth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Getter
@SuppressWarnings("serial")
public class StorageException extends RuntimeException {
    private final HttpStatus status;
    @Nullable
    private final String reason;

    public StorageException(HttpStatus status, String reason) {
        super(reason);
        this.status = status;
        this.reason = reason;
    }
}
