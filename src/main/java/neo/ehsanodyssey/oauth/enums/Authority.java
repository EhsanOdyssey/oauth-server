package neo.ehsanodyssey.oauth.enums;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public enum Authority implements GrantedAuthority {

    ROLE_ADMIN, ROLE_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
