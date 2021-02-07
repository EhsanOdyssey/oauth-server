package neo.ehsanodyssey.oauth.service;

import neo.ehsanodyssey.oauth.config.AppProperties;
import neo.ehsanodyssey.oauth.dto.AuthClientDetailsModel;
import neo.ehsanodyssey.oauth.dto.ResponseModel;
import neo.ehsanodyssey.oauth.entity.AuthClientDetails;
import neo.ehsanodyssey.oauth.exception.ResourceNotFoundException;
import neo.ehsanodyssey.oauth.repository.AuthClientDetailsRepository;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Service
public class AuthClientDetailsService implements ClientDetailsService {

    private final AuthClientDetailsRepository authClientDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final MessageSource messageSource;

    public AuthClientDetailsService(AuthClientDetailsRepository authClientDetailsRepository,
                                    PasswordEncoder passwordEncoder, AppProperties appProperties, MessageSource messageSource) {
        this.authClientDetailsRepository = authClientDetailsRepository;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
        this.messageSource = messageSource;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
//        return authClientDetailsRepository.findByClientId(clientId).orElseThrow(IllegalArgumentException::new);
        String message = messageSource.getMessage("app.error.wrong.client.id", new Object[] {clientId}, LocaleContextHolder.getLocale());
        return authClientDetailsRepository.findByClientId(clientId).orElseThrow(() ->
                new ResourceNotFoundException(HttpStatus.NOT_FOUND, new ResponseModel(false, message)));
    }

    public AuthClientDetailsModel create(AuthClientDetailsModel dto) {
        AuthClientDetails entity = authClientDetailsRepository.save(
                new AuthClientDetails.AuthClientDetailsBuilder(
                        dto.getClientId(),
                        passwordEncoder.encode(dto.getClientSecret()),
                        dto.getGrantTypes(),
                        dto.getScopes())
                        .withAuthorities(dto.getAuthorities())
                        .withResources(dto.getResourceIds())
                        .withRedirectUris(dto.getRedirectUris())
                        .withTokenExpiry(dto.getAccessTokenValiditySeconds() != null ?
                                dto.getAccessTokenValiditySeconds() :
                                appProperties.getOauth2().getAccessTokenValiditySeconds())
                        .withRefreshTokenExpiry(dto.getRefreshTokenValiditySeconds() != null ?
                                dto.getRefreshTokenValiditySeconds() :
                                appProperties.getOauth2().getRefreshTokenValiditySeconds())
                        .build());
        return toDto(entity);
    }

    private AuthClientDetailsModel toDto(AuthClientDetails entity) {
        return new AuthClientDetailsModel(
                entity.getClientId(),
                entity.getClientSecret(),
                entity.getAuthorizedGrantTypes(),
                entity.getScope(),
                entity.getAuthorities().parallelStream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toSet()),
                entity.getResourceIds(),
                entity.getRegisteredRedirectUri(),
                entity.getAccessTokenValiditySeconds(),
                entity.getRefreshTokenValiditySeconds());
    }
}
