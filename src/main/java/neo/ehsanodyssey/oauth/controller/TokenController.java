package neo.ehsanodyssey.oauth.controller;

import neo.ehsanodyssey.oauth.controller.helper.PrincipalController;
import neo.ehsanodyssey.oauth.dto.AuthClientDetailsModel;
import neo.ehsanodyssey.oauth.dto.ResponseModel;
import neo.ehsanodyssey.oauth.dto.RevokeTokenModel;
import neo.ehsanodyssey.oauth.exception.ResourceNotFoundException;
import neo.ehsanodyssey.oauth.service.AuthClientDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import java.util.stream.Collectors;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Api(tags = "AUTH: Token actions")
@RestController
@RequestMapping("/oauth")
@Validated
public class TokenController extends PrincipalController {

    private final DefaultTokenServices tokenServices;
    private final TokenStore tokenStore;
    private final AuthClientDetailsService authClientDetailsService;
    private final MessageSource messageSource;

    public TokenController(DefaultTokenServices tokenServices, TokenStore tokenStore,
                           AuthClientDetailsService authClientDetailsService, MessageSource messageSource) {
        this.tokenServices = tokenServices;
        this.tokenStore = tokenStore;
        this.authClientDetailsService = authClientDetailsService;
        this.messageSource = messageSource;
    }

    @ApiOperation(value = "Create AuthClient details by Admin user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, response = AuthClientDetailsModel.class, message = "Success"),
            @ApiResponse(code = 400, response = ResponseModel.class, message = "Bad Request"),
            @ApiResponse(code = 500, response = ResponseModel.class, message = "Server Error.")
    })
    @PreAuthorize("#oauth2.clientHasAnyRole('ROLE_ADMIN')")
    @PostMapping("/clients")
    public ResponseEntity createClient(@Valid @RequestBody AuthClientDetailsModel authClientDetails, Locale locale) {
        return new ResponseEntity<>(new ResponseModel(true,
                messageSource.getMessage("app.message.success", null, locale),
                authClientDetailsService.create(authClientDetails)), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Revoke token by Admin user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = ResponseModel.class, message = "Token has been revoked."),
            @ApiResponse(code = 400, response = ResponseModel.class, message = "Bad Request"),
            @ApiResponse(code = 404, response = ResponseModel.class, message = "Invalid Token.")
    })
    @PreAuthorize("#oauth2.clientHasAnyRole('ROLE_ADMIN')")
    @PostMapping("/revoke")
    public ResponseEntity revokeToken(@Valid @RequestBody RevokeTokenModel revokeTokenModel, Locale locale) {
        boolean revoke = tokenServices.revokeToken(revokeTokenModel.getTokenId());
        if (revoke) {
            return new ResponseEntity<>(new ResponseModel(true, messageSource.getMessage("app.message.revoke.token", null, locale), null), HttpStatus.OK);
        }
        throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, new ResponseModel(true, messageSource.getMessage("app.message.revoke.token", null, locale), null));
    }

    @ApiOperation(value = "Fetch all tokens based on clientId and username (Optional) by Admin user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = ResponseModel.class, message = "Success")
    })
    @PreAuthorize("#oauth2.clientHasAnyRole('ROLE_ADMIN')")
    @GetMapping("/gettokens")
    public ResponseEntity getTokens(@RequestParam String clientId, @RequestParam(required = false) String username, Locale locale) {
        Collection<OAuth2AccessToken> tokens;
        if (!StringUtils.isEmpty(username)) {
            tokens = tokenStore.findTokensByClientIdAndUserName(clientId, username);
        } else {
            tokens = tokenStore.findTokensByClientId(clientId);
        }
        return new ResponseEntity<>(new ResponseModel(true, messageSource.getMessage("app.message.success", null, locale),
                Optional.ofNullable(tokens).orElse(Collections.emptyList()).parallelStream()
                        .map(OAuth2AccessToken::getValue).collect(Collectors.toList())), HttpStatus.OK);
    }
}