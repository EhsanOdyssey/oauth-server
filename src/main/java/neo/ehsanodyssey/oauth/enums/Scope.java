package neo.ehsanodyssey.oauth.enums;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public enum Scope {
    READ, WRITE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
