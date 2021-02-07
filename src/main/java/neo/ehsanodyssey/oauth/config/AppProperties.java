package neo.ehsanodyssey.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final DefaultLocal defaultLocale = new DefaultLocal();
    private final Files files = new Files();
    private final OAuth2 oauth2 = new OAuth2();

    public static final class DefaultLocal {
        private String lang;
        private String country;

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    public static final class Files {
        private String basePath;

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
    }

    public static final class OAuth2 {
        private Integer accessTokenValiditySeconds;
        private Integer refreshTokenValiditySeconds;

        public Integer getAccessTokenValiditySeconds() {
            return accessTokenValiditySeconds;
        }

        public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
            this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        }

        public Integer getRefreshTokenValiditySeconds() {
            return refreshTokenValiditySeconds;
        }

        public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
            this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
        }
    }

    public DefaultLocal getDefaultLocale() {
        return defaultLocale;
    }

    public Files getFiles() {
        return files;
    }

    public OAuth2 getOauth2() {
        return oauth2;
    }
}
