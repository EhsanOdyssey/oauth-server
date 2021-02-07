package neo.ehsanodyssey.oauth.config.oauth.token;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public class CustomJdbcTokenStore extends JdbcTokenStore {

    public CustomJdbcTokenStore(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected byte[] serializeAccessToken(OAuth2AccessToken token) {
        return CustomSerializationUtils.serialize(token);
    }

    @Override
    protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
        return CustomSerializationUtils.serialize(token);
    }

    @Override
    protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
        return CustomSerializationUtils.serialize(authentication);
    }

    @Override
    protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return CustomSerializationUtils.deserialize(token);
    }

    @Override
    protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
        return CustomSerializationUtils.deserialize(token);
    }

    @Override
    protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return CustomSerializationUtils.deserialize(authentication);
    }

    private static class CustomSerializationUtils {
        private static final List<String> ALLOWED_CLASSES;

        static {
            List<String> classes = new ArrayList<>();
            classes.add("java.lang.");
            classes.add("java.util.");
            classes.add("org.springframework.security.");
            classes.add("com.safarmarket.security.");
            ALLOWED_CLASSES = Collections.unmodifiableList(classes);
        }

        static byte[] serialize(Object state) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(state);
                oos.flush();
                return bos.toByteArray();
            }
            catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        static <T> T deserialize(byte[] byteArray) {
            try (ObjectInputStream oip = new CustomSaferObjectInputStream(new ByteArrayInputStream(byteArray),
                    Thread.currentThread().getContextClassLoader(), ALLOWED_CLASSES)) {
                @SuppressWarnings("unchecked")
                T result = (T) oip.readObject();
                return result;
            }
            catch (IOException | ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }

        private static class CustomSaferObjectInputStream extends ObjectInputStream {
            private final List<String> allowedClasses;
            private final ClassLoader classLoader;

            CustomSaferObjectInputStream(InputStream in, ClassLoader classLoader, List<String> allowedClasses) throws IOException {
                super(in);
                this.classLoader = classLoader;
                this.allowedClasses = Collections.unmodifiableList(allowedClasses);
            }

            @Override
            protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
                if (isProhibited(classDesc.getName())) {
                    throw new NotSerializableException("Not allowed to custom deserialize " + classDesc.getName());
                }
                if (this.classLoader != null) {
                    return ClassUtils.forName(classDesc.getName(), this.classLoader);
                }
                return super.resolveClass(classDesc);
            }

            private boolean isProhibited(String className) {
                for (String allowedClass : this.allowedClasses) {
                    if (className.startsWith(allowedClass)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }
}
