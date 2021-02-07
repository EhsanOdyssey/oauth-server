package neo.ehsanodyssey.oauth.config.swagger;

import com.google.common.collect.Lists;

import neo.ehsanodyssey.oauth.dto.RevokeTokenModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 16 Dec 2019
 */
@Configuration
@EnableSwagger2
@ComponentScan(basePackages = "neo.ehsanodyssey.oauth.controller")
public class SwaggerConfig {

    private OAuth securityScheme;

    private SecurityContext securityContext;

    public SwaggerConfig(OAuth securityScheme, SecurityContext securityContext) {
        this.securityScheme = securityScheme;
        this.securityContext = securityContext;
    }

    private static final String API_PATH = "/users/**";
    private static final String OAUTH_PATH = "/oauth/**";

    @Bean
    public Docket userManagementApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("User Management")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("(?!/fondos).+"))
                .paths(PathSelectors.regex("(?!/error).+"))
                .paths(PathSelectors.regex("(?!/oauth/).+"))
                .build()
                .securitySchemes(Lists.newArrayList(securityScheme))
                .securityContexts(Lists.newArrayList(securityContext));
    }

    @Bean
    public Docket authenticationApi() {
        Class[] clazz = {RevokeTokenModel.class, ResponseEntity.class, OAuth2RefreshToken.class, OAuth2AccessToken.class};
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("OAuth2")
                .apiInfo(oauthInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("(?!/users).+"))
                .paths(PathSelectors.regex("(?!/helper).+"))
                .paths(PathSelectors.regex("(?!/fondos).+"))
                .paths(PathSelectors.regex("(?!/error).+"))
                .paths(PathSelectors.regex("(?!/oauth/error).+"))
                .paths(PathSelectors.regex("(?!/oauth/authorize).+"))
                .paths(PathSelectors.regex("(?!/oauth/confirm_access).+"))
                .paths(PathSelectors.regex("(?!/oauth/token_key).+"))
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("EhsanOdyssey", "http://example.com", "info@example.com");
        return new ApiInfoBuilder()
                .title("OAuth2 Application REST API")
                .description("OAuth2 Server Application")
                .version("1.0")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .contact(contact)
                .build();
    }

    private ApiInfo oauthInfo() {
        Contact contact = new Contact("EhsanOdyssey", "http://example.com", "info@example.com");
        return new ApiInfoBuilder()
                .title("OAuth2 Server Application REST API")
                .description("Authentication & Authorization APIs Applications")
                .version("1.0")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .contact(contact)
                .build();
    }
}