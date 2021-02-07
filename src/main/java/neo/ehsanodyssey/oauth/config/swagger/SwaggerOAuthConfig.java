package neo.ehsanodyssey.oauth.config.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import springfox.documentation.OperationNameGenerator;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;

import java.util.*;

import static com.google.common.collect.Sets.newHashSet;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 16 Dec 2019
 */
@Configuration
public class SwaggerOAuthConfig {

    @Value("${swagger.oauth2.uri}")
    private String oauth2URI;

    @Bean
    public SecurityScheme apiKey() {
        return new ApiKey(HttpHeaders.AUTHORIZATION, "apiKey", "header");
    }

    @Bean
    public SecurityScheme apiCookieKey() {
        return new ApiKey(HttpHeaders.COOKIE, "apiKey", "cookie");
    }

    @Bean
    public OAuth securityScheme() {

        List<AuthorizationScope> authorizationScopeList = Lists.newArrayList();
        authorizationScopeList.add(new AuthorizationScope("read", "read all"));
        authorizationScopeList.add(new AuthorizationScope("write", "access all"));

        List<GrantType> grantTypes = Lists.newArrayList();
        GrantType passwordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant(oauth2URI);
        grantTypes.add(passwordCredentialsGrant);

        return new OAuth("oauth2", authorizationScopeList, grantTypes);
    }

    @Bean
    public SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(securityReferences())
                .build();
    }

    private List<SecurityReference> securityReferences() {
        return Collections.singletonList(new SecurityReference("oauth2", scopes()));
    }

    private AuthorizationScope[] scopes() {
        return new AuthorizationScope[] {
                new AuthorizationScope("read", "for read operations"),
                new AuthorizationScope("write", "for write operations")
        };
    }

    @Bean
    public SecurityConfiguration security() {
        return new SecurityConfiguration("adminapp", "123", "", "", "Bearer access token", ApiKeyVehicle.HEADER, HttpHeaders.AUTHORIZATION, "");
    }

    @Bean
    @Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
    public ApiListingScannerPlugin listingScanner() {
        return new SwaggerManualApiPlugin(new CachingOperationNameGenerator());
    }

    static class SwaggerManualApiPlugin implements ApiListingScannerPlugin {
        private final CachingOperationNameGenerator cachingOperationNameGenerator;

        public SwaggerManualApiPlugin(CachingOperationNameGenerator cachingOperationNameGenerator) {
            this.cachingOperationNameGenerator = cachingOperationNameGenerator;
        }

        @Override
        public List<ApiDescription> apply(DocumentationContext context) {
            return Arrays.asList(
                    new ApiDescription(
                            "/oauth/token",
                            "Generate Token",
                            postOperations(
                                    cachingOperationNameGenerator,
                                    "password_refresh_token",
                                    HttpMethod.POST,
                                    "Token Endpoint by POST",
                                    new ModelRef("string")),
                            false
                    )
                    , new ApiDescription(
                            "checktoken",
                            "/oauth/validate",
                            "Check Token",
                            checkOperations(
                                    cachingOperationNameGenerator,
                                    "validate_token",
                                    HttpMethod.POST,
                                    "Check Token Endpoint by POST",
                                    new ModelRef("string")),
                            false
                    )
            );
        }

        private static List<Operation> getOperations(OperationNameGenerator operationNameGenerator,
                                                     String codegenMethodName, HttpMethod method, String note,
                                                     ModelReference responseModel) {
            return Arrays.asList(
                    operationGenerator(
                            operationNameGenerator,
                            method,
                            codegenMethodName,
                            note,
                            newHashSet("Default Get Token"),
                            getRequestParameters(),
                            responseModel,
                            responseMessages()
                    )
            );
        }

        private static List<Operation> checkOperations(OperationNameGenerator operationNameGenerator,
                                                     String codegenMethodName, HttpMethod method, String note,
                                                     ModelReference responseModel) {
            return Arrays.asList(
                    operationGenerator(
                            operationNameGenerator,
                            method,
                            codegenMethodName,
                            note,
                            newHashSet("AUTH: Check Token"),
                            checkRequestParameters(),
                            responseModel,
                            responseMessages()
                    )
            );
        }

        private static List<Operation> postOperations(OperationNameGenerator operationNameGenerator,
                                                      String codegenMethodName, HttpMethod method, String note,
                                                      ModelReference responseModel) {
            return Arrays.asList(
                    operationGenerator(
                            operationNameGenerator,
                            method,
                            codegenMethodName,
                            note,
                            newHashSet("AUTH: Get Token"),
                            postRequestParameters(),
                            responseModel,
                            responseMessages()
                    )
            );
        }

        private static List<Parameter> getRequestParameters() {
            return Arrays.asList(
                    parameterGenerator(
                            "Basic Auth EX: convert [clientId:clientSecret] (adminapp:123) by online Base64 converter (www.base64encode.org)",
                            1,
                            String.class,
                            "Authorization",
                            "Basic YWRtaW5hcHA6MTIz",
                            "header",
                            "access",
                            true,
                            true,
                            new ModelRef("string")
                    )
            );
        }

        private static List<Parameter> checkRequestParameters() {
            return Arrays.asList(
                    parameterGenerator(
                            "The Token Value",
                            1,
                            String.class,
                            "token",
                            "",
                            "query",
                            "access",
                            true,
                            true,
                            new ModelRef("string")
                    )
            );
        }

        private static List<Parameter> postRequestParameters() {
            return Arrays.asList(
                    parameterGenerator(
                            "Basic Auth EX: convert [clientId:clientSecret] (adminapp:123) by online Base64 converter (www.base64encode.org)",
                            1,
                            String.class,
                            "Authorization",
                            "Basic YWRtaW5hcHA6MTIz",
                            "header",
                            "access",
                            true,
                            true,
                            new ModelRef("string")
                    ),
                    parameterGenerator(
                            "The Grant Type EX: password for getting token and refresh_token for refreshing the token by refresh_token value",
                            2,
                            String.class,
                            "grant_type",
                            "password",
                            "query",
                            "access",
                            true,
                            true,
                            new ModelRef("string")
                    ),
                    parameterGenerator(
                            "The Username (For grand_type=password)",
                            3,
                            String.class,
                            "username",
                            "neo",
                            "query",
                            "access",
                            true,
                            false,
                            new ModelRef("string")
                    ),
                    parameterGenerator(
                            "The Password (For grand_type=password)",
                            4,
                            String.class,
                            "password",
                            "123",
                            "query",
                            "access",
                            true,
                            false,
                            new ModelRef("string")
                    ),
                    parameterGenerator(
                            "The Refresh Token (For grand_type=refresh_token)",
                            5,
                            String.class,
                            "refresh_token",
                            "",
                            "query",
                            "access",
                            true,
                            false,
                            new ModelRef("string")
                    )
            );
        }

        private static Operation operationGenerator(OperationNameGenerator operationNameGenerator, HttpMethod method,
                                                    String codegenMethodName, String note, Set<String> tags,
                                                    List<Parameter> parameters, ModelReference responseModel,
                                                    Set<ResponseMessage> responseMessages) {
            return new OperationBuilder(operationNameGenerator)
                    .authorizations(new ArrayList())
                    .method(method)
                    .codegenMethodNameStem(codegenMethodName)
                    .notes(note)
                    .responseModel(responseModel)
                    .tags(tags)
                    .parameters(parameters)
                    .responseMessages(responseMessages)
                    .build();
        }

        private static Parameter parameterGenerator(String description, int order, Class<?> type, String name,
                                                    String defaultValue, String parameterType, String parameterAccess,
                                                    boolean hidden, boolean required, ModelReference modelRef) {
            return new ParameterBuilder()
                    .description(description)
                    .order(order)
                    .type(new TypeResolver().resolve(type))
                    .name(name)
                    .defaultValue(defaultValue)
                    .parameterType(parameterType)
                    .parameterAccess(parameterAccess)
                    .hidden(hidden)
                    .required(required)
                    .modelRef(modelRef)
                    .build();
        }

        private static Set<ResponseMessage> responseMessages() {
            return newHashSet(new ResponseMessageBuilder()
                    .code(200)
//                    .message("Successfully received bug 1767 or 2219 response")
                    .responseModel(new ModelRef(OAuth2AccessToken.class.getSimpleName()))
                    .build());
        }

        @Override
        public boolean supports(DocumentationType delimiter) {
            return DocumentationType.SWAGGER_2.equals(delimiter);
        }
    }
}
