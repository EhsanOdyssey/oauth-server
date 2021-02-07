package neo.ehsanodyssey.oauth.repository;

import neo.ehsanodyssey.oauth.entity.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Repository
public interface FileRepository extends MongoRepository<File, Long> {
}
