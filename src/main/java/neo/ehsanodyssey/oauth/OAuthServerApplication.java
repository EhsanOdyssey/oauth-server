package neo.ehsanodyssey.oauth;

import neo.ehsanodyssey.oauth.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class OAuthServerApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(OAuthServerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(OAuthServerApplication.class, args);
    }
}