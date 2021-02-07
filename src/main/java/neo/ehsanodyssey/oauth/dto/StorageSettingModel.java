package neo.ehsanodyssey.oauth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@ToString
@Getter
public class StorageSettingModel implements Serializable {

    private final String type;
    private final String path;
    @JsonProperty("max_size")
    private final String maxSize;
    @JsonProperty("accepted_types")
    private final String[] acceptedTypes;

    @JsonCreator
    public StorageSettingModel(String type, String path, String maxSize, String[] acceptedTypes) {
        this.type = type;
        this.path = path;
        this.maxSize = maxSize;
        this.acceptedTypes = acceptedTypes;
    }
}
