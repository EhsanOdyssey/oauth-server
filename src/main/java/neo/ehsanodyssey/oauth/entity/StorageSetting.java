package neo.ehsanodyssey.oauth.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Document(collection = "storage_settings")
@ToString
@Getter
@Setter
public class StorageSetting implements Serializable {

    private static final long RATIO = 1024L;

    @Id
    private String id;
    private String type;
    private String path;
    private long maxSize;
    private String acceptedTypes;

    public StorageSetting() {
    }

    public StorageSetting(String type, String path, long maxSize, String acceptedTypes) {
        this.type = type;
        this.path = path;
        this.maxSize = maxSize;
        this.acceptedTypes = acceptedTypes;
    }

    public StorageSetting(String type, String path, String maxSize, String[] acceptedTypes) {
        this.type = type;
        this.path = path;
        char unit = maxSize.charAt(maxSize.length() - 1);
        long maxSizeLong = Long.parseLong(maxSize.substring(0, maxSize.length() - 1));
        if (unit == 'K') {
            this.maxSize = maxSizeLong * RATIO;
        } else if (unit == 'M') {
            this.maxSize = maxSizeLong * RATIO * RATIO;
        } else {
            this.maxSize = RATIO * RATIO;
        }
        this.acceptedTypes = String.join(",", acceptedTypes);
    }

    public Path getStorageLocation(String fileName) {
        return Paths.get(String.format("%s/%s", this.path, fileName));
    }
}
