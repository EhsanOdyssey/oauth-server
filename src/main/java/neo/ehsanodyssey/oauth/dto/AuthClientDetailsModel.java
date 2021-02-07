package neo.ehsanodyssey.oauth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import neo.ehsanodyssey.oauth.enums.Authority;
import neo.ehsanodyssey.oauth.enums.GrantType;
import neo.ehsanodyssey.oauth.enums.Scope;
import neo.ehsanodyssey.oauth.enums.EnumType;
import neo.ehsanodyssey.oauth.validator.Enumerated;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Getter
public final class AuthClientDetailsModel implements Serializable {
    @NotEmpty
    @JsonProperty("client_id")
    private final String clientId;
    @NotEmpty
    @JsonProperty(value = "client_secret", access = JsonProperty.Access.WRITE_ONLY)
    private final String clientSecret;
    @NotEmpty
    @Enumerated(eClass = GrantType.class, eType = EnumType.COLLECTION)
    @JsonProperty("grant_types")
    private final Set<String> grantTypes;
    @Enumerated(eClass = Scope.class, eType = EnumType.COLLECTION)
    @NotEmpty
    @JsonProperty("scopes")
    private final Set<String> scopes;
    @Enumerated(eClass = Authority.class, eType = EnumType.COLLECTION)
    private final Set<String> authorities;
    @JsonProperty("resources")
    private final Set<String> resourceIds;
    @JsonProperty("redirect_uris")
    private final Set<String> redirectUris;
    @NotNull
    @JsonProperty(value = "expiry_seconds")
    private final Integer accessTokenValiditySeconds;
    @NotNull
    @JsonProperty(value = "refresh_expiry_seconds")
    private final Integer refreshTokenValiditySeconds;

    @JsonCreator
    public AuthClientDetailsModel(String clientId, String clientSecret, Set<String> grantTypes, Set<String> scopes,
                                  Set<String> authorities, Set<String> resourceIds, Set<String> redirectUris,
                                  Integer accessTokenValiditySeconds, Integer refreshTokenValiditySeconds) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantTypes = grantTypes;
        this.scopes = scopes;
        this.authorities = authorities;
        this.resourceIds = resourceIds;
        this.redirectUris = redirectUris;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    @Override
    public String toString() {
        return "AuthClientDetailsModel {" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", grantTypes=" + grantTypes +
                ", scopes=" + scopes +
                ", authorities=" + authorities +
                ", resourceIds=" + resourceIds +
                ", redirectUris=" + redirectUris +
                ", accessTokenValiditySeconds=" + accessTokenValiditySeconds +
                ", refreshTokenValiditySeconds=" + refreshTokenValiditySeconds +
                '}';
    }
}
