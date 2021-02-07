package neo.ehsanodyssey.oauth.enums;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public enum GrantType {
    PASSWORD, REFRESH_TOKEN, AUTHORIZATION_CODE, IMPLICIT, CLIENT_CREDENTIALS;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
