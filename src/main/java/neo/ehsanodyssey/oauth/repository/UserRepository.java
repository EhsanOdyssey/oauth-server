package neo.ehsanodyssey.oauth.repository;

import neo.ehsanodyssey.oauth.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsernameOrEmailOrPhoneNumber(String username, String email, String phonenumber);
}
