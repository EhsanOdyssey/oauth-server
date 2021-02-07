package neo.ehsanodyssey.oauth.config.mongo.changelogs;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import neo.ehsanodyssey.oauth.entity.AuthClientDetails;
import neo.ehsanodyssey.oauth.entity.User;
import neo.ehsanodyssey.oauth.enums.Authority;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 16 Dec 2019
 */
@ChangeLog(order = "1")
public class MongoChangeLog {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @ChangeSet(order = "001", id = "insertAdminAppClientDetails", author = "EhsanOdyssey")
    public void insertAdminAppClientDetails(MongoTemplate mongoTemplate, Environment environment) {
        String tokenExpiryEnv = environment.getProperty("app.oauth2.access-token-validity-seconds");
        int tokenExpiry = StringUtils.hasText(tokenExpiryEnv) ? Integer.parseInt(tokenExpiryEnv) : 0;
        String refTokenExpiryEnv = environment.getProperty("app.oauth2.refresh-token-validity-seconds");
        int refTokenExpiry = StringUtils.hasText(refTokenExpiryEnv) ? Integer.parseInt(refTokenExpiryEnv) : 0;
        AuthClientDetails adminClientDetails =
                new AuthClientDetails.AuthClientDetailsBuilder("adminapp", passwordEncoder.encode("123"),
                        "password,refresh_token", "read,write")
                .withCommaSeparatorAuthorities(Authority.ROLE_ADMIN.getAuthority())
                .withTokenExpiry(tokenExpiry)
                .withRefreshTokenExpiry(refTokenExpiry)
                        .build();
        mongoTemplate.save(adminClientDetails);
    }

    @ChangeSet(order = "002", id = "insertAdminUserToTestAuthentication", author = "EhsanOdyssey")
    public void insertAdminUserToTestAuthentication(MongoTemplate mongoTemplate) {
        User user = new User.UserBuilder(
                "neo", "", "", passwordEncoder.encode("123"), Set.of(Authority.ROLE_ADMIN.getAuthority())).build();
        mongoTemplate.save(user);
    }

}
