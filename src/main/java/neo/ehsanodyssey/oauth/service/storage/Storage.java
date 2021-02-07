package neo.ehsanodyssey.oauth.service.storage;

import neo.ehsanodyssey.oauth.entity.File;
import neo.ehsanodyssey.oauth.entity.StorageSetting;
import org.springframework.core.io.InputStreamSource;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public interface Storage<T extends InputStreamSource, U extends InputStreamSource> {

    void store(T inputStreamSource, String fileName, StorageSetting storageSetting);
    U read(File file);
    void delete(File file);
}
