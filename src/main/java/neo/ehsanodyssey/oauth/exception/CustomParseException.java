package neo.ehsanodyssey.oauth.exception;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public class CustomParseException extends RuntimeException {
    private String filed;
    private Class<?> clazz;

    public CustomParseException(String msg, Throwable t) {
        super(msg, t);
    }

    public CustomParseException(String msg) {
        super(msg);
    }

    public CustomParseException(String filed, String msg) {
        super(msg);
        this.filed = filed;
    }

    public CustomParseException(String filed, Class<?> clazz, String msg, Throwable t) {
        super(msg, t);
        this.filed = filed;
        this.clazz = clazz;
    }

    public String getFiled() {
        return filed;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
