package neo.ehsanodyssey.oauth.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Getter
@Setter
public class RevokeTokenModel implements Serializable {
    @NotEmpty
    private String tokenId;
}
