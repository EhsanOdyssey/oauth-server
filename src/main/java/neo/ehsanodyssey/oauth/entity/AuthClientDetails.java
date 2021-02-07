package neo.ehsanodyssey.oauth.entity;

import neo.ehsanodyssey.oauth.enums.Authority;
import neo.ehsanodyssey.oauth.enums.GrantType;
import neo.ehsanodyssey.oauth.enums.Scope;
import neo.ehsanodyssey.oauth.exception.CustomParseException;
import neo.ehsanodyssey.oauth.utils.ExceptionUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Document(collection = "auth_client_details")
@Getter
@Setter
public class AuthClientDetails implements ClientDetails {

    @Id
    private String id;
    @Field("client_id")
    @NotEmpty
    private String clientId;
    @Field("client_secret")
    @NotEmpty
    private String clientSecret;
    @Field("grant_types")
    @NotEmpty
    private String authorizedGrantTypes;
    @NotEmpty
    private String scopes;
    private String authorities;
    @Field("resources")
    private String resourceIds;
    @Field("redirect_uris")
    private String registeredRedirectUri;
    @Field("token_expiry")
    private Integer accessTokenValiditySeconds;
    @Field("refresh_token_expiry")
    private Integer refreshTokenValiditySeconds;
    @Field("additional_information")
    private String additionalInformation;

    public AuthClientDetails() {
    }

    private AuthClientDetails(AuthClientDetailsBuilder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.authorizedGrantTypes = builder.authorizedGrantTypes;
        this.scopes = builder.scopes;
        this.authorities = builder.authorities;
        this.resourceIds = builder.resourceIds;
        this.registeredRedirectUri = builder.registeredRedirectUri;
        this.accessTokenValiditySeconds = builder.accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = builder.refreshTokenValiditySeconds;
        this.additionalInformation = builder.additionalInformation;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return StringUtils.commaDelimitedListToSet(authorizedGrantTypes);
    }

    @Override
    public Set<String> getScope() {
        return StringUtils.commaDelimitedListToSet(scopes);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return StringUtils.commaDelimitedListToSet(authorities).parallelStream()
                .map(Authority::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getResourceIds() {
        return StringUtils.commaDelimitedListToSet(resourceIds);
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return StringUtils.commaDelimitedListToSet(registeredRedirectUri);
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        if (StringUtils.hasText(additionalInformation)) {
            return Arrays.stream(additionalInformation.split(","))
                    .map(entry -> entry.split("="))
                    .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    public boolean isScoped() {
        return this.scopes != null && !this.scopes.isEmpty();
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return true;
    }

    @Override
    public String toString() {
        return "AuthClientDetails {" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", authorizedGrantTypes='" + authorizedGrantTypes + '\'' +
                ", scopes='" + scopes + '\'' +
                ", authorities=" + authorities +
                ", resourceIds='" + resourceIds + '\'' +
                ", registeredRedirectUri='" + registeredRedirectUri + '\'' +
                ", accessTokenValiditySeconds=" + accessTokenValiditySeconds +
                ", refreshTokenValiditySeconds=" + refreshTokenValiditySeconds +
                ", additionalInformation='" + additionalInformation + '\'' +
                '}';
    }

    public static class AuthClientDetailsBuilder {
        private String clientId;
        private String clientSecret;
        private String authorizedGrantTypes;
        private String scopes;
        private String authorities;
        private String resourceIds;
        private String registeredRedirectUri;
        private Integer accessTokenValiditySeconds;
        private Integer refreshTokenValiditySeconds;
        private String additionalInformation;

        public AuthClientDetailsBuilder(String clientId, String clientSecret, String grantTypes, String scopes) {
            if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(clientSecret) ||
                    StringUtils.isEmpty(grantTypes) || StringUtils.isEmpty(scopes)) {
                throw new IllegalArgumentException("app.error.null.mandatory.fields.constructor");
            }
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            try {
                Set<GrantType> grantTypeSet = StringUtils.commaDelimitedListToSet(grantTypes).parallelStream()
                        .map(String::trim).map(String::toUpperCase)
                        .map(GrantType::valueOf).collect(Collectors.toSet());
                this.authorizedGrantTypes = !grantTypeSet.isEmpty() ? grantTypes : null;
            } catch (IllegalArgumentException e) {
                throw new CustomParseException("grant_types", GrantType.class, ExceptionUtils.APP_ERROR_PARSE_ATTRIBUTE, e);
            }
            try {
                Set<Scope> scopeSet = StringUtils.commaDelimitedListToSet(scopes).parallelStream()
                        .map(String::trim).map(String::toUpperCase).map(Scope::valueOf).collect(Collectors.toSet());
                this.scopes = !scopeSet.isEmpty() ? scopes : null;
            } catch (IllegalArgumentException e) {
                throw new CustomParseException("scopes", Scope.class, ExceptionUtils.APP_ERROR_PARSE_ATTRIBUTE, e);
            }
        }

        public AuthClientDetailsBuilder(String clientId, String clientSecret, Set<String> grantTypes, Set<String> scopes) {
            if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(clientSecret) ||
                    grantTypes == null || grantTypes.isEmpty() || scopes == null || scopes.isEmpty()) {
                throw new IllegalArgumentException("app.error.null.mandatory.fields.constructor");
            }
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            try {
                Set<GrantType> grantTypeSet = grantTypes.parallelStream()
                        .map(String::toUpperCase).map(GrantType::valueOf).collect(Collectors.toSet());
                this.authorizedGrantTypes = !grantTypeSet.isEmpty() ? grantTypeSet.parallelStream()
                        .map(GrantType::toString).collect(Collectors.joining(",")) : null;
            } catch (IllegalArgumentException e) {
                throw new CustomParseException("grant_types", GrantType.class, ExceptionUtils.APP_ERROR_PARSE_ATTRIBUTE, e);
            }
            try {
                Set<Scope> scopeSet = scopes.parallelStream()
                        .map(String::toUpperCase).map(Scope::valueOf).collect(Collectors.toSet());
                this.scopes = !scopeSet.isEmpty() ? scopeSet.parallelStream()
                        .map(Scope::toString).collect(Collectors.joining(",")) : null;
            } catch (IllegalArgumentException e) {
                throw new CustomParseException("scopes", Scope.class, ExceptionUtils.APP_ERROR_PARSE_ATTRIBUTE, e);
            }
        }

        public AuthClientDetailsBuilder withTokenExpiry(int expiry) {
            this.accessTokenValiditySeconds = expiry;
            return this;
        }

        public AuthClientDetailsBuilder withRefreshTokenExpiry(int expiry) {
            this.refreshTokenValiditySeconds = expiry;
            return this;
        }

        public AuthClientDetailsBuilder withAuthorities(Set<String> authorities) {
            if (authorities != null && !authorities.isEmpty()) {
                try {
                    Set<Authority> authoritySet = authorities.parallelStream()
                            .map(String::toUpperCase).map(Authority::valueOf).collect(Collectors.toSet());
                    this.authorities = !authoritySet.isEmpty() ? authoritySet.parallelStream()
                            .map(Authority::toString).collect(Collectors.joining(",")) : null;
                } catch (IllegalArgumentException e) {
                    throw new CustomParseException("authorities", Authority.class, ExceptionUtils.APP_ERROR_PARSE_ATTRIBUTE, e);
                }
            }
            return this;
        }

        public AuthClientDetailsBuilder withAuthoritiesEnum(Set<Authority> authorities) {
            if (authorities != null && !authorities.isEmpty()) {
                this.authorities = authorities.parallelStream()
                        .map(Authority::toString).collect(Collectors.joining(","));
            }
            return this;
        }

        public AuthClientDetailsBuilder withCommaSeparatorAuthorities(String authorities) {
            if (StringUtils.hasText(authorities)) {
                try {
                    Set<Authority> authoritySet = StringUtils.commaDelimitedListToSet(authorities).parallelStream()
                            .map(String::trim).map(String::toUpperCase)
                            .map(Authority::valueOf).collect(Collectors.toSet());
                    this.authorities = !authoritySet.isEmpty() ? authoritySet.parallelStream()
                            .map(Authority::toString).collect(Collectors.joining(",")) : null;
                } catch (IllegalArgumentException e) {
                    throw new CustomParseException("authorities", Authority.class, ExceptionUtils.APP_ERROR_PARSE_ATTRIBUTE, e);
                }
            }
            return this;
        }

        public AuthClientDetailsBuilder withResources(Set<String> resources) {
            if (resources != null && !resources.isEmpty()) {
                this.resourceIds = resources.parallelStream().collect(Collectors.joining(","));
            }
            return this;
        }

        public AuthClientDetailsBuilder withCommaSeparatorResources(String resources) {
            this.resourceIds = resources;
            return this;
        }

        public AuthClientDetailsBuilder withRedirectUris(Set<String> redirectUris) {
            if (redirectUris != null && !redirectUris.isEmpty()) {
                this.registeredRedirectUri = redirectUris.parallelStream().collect(Collectors.joining(","));
            }
            return this;
        }

        public AuthClientDetailsBuilder withCommaSeparatorRedirectUris(String redirectUris) {
            this.registeredRedirectUri = redirectUris;
            return this;
        }

        public AuthClientDetailsBuilder withAdditionalInformation(Map<String, String> additionalInformation) {
            if (additionalInformation != null && !additionalInformation.isEmpty()) {
                this.additionalInformation = additionalInformation.keySet().stream()
                        .map(key -> key + "=" + additionalInformation.get(key))
                        .collect(Collectors.joining(", ", "{", "}"));
            }
            return this;
        }

        public AuthClientDetails build() {
            return new AuthClientDetails(this);
        }
    }

}
