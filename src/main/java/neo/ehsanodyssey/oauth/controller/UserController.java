package neo.ehsanodyssey.oauth.controller;

import neo.ehsanodyssey.oauth.controller.helper.PrincipalController;
import neo.ehsanodyssey.oauth.dto.ResponseModel;
import neo.ehsanodyssey.oauth.dto.UserModel;
import neo.ehsanodyssey.oauth.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Api(tags = "USERS: Management actions")
@RestController
@RequestMapping("/users")
@Validated
public class UserController extends PrincipalController {

    private final TokenStore tokenStore;
    private final UserService userService;
    private final MessageSource messageSource;

    public UserController(TokenStore tokenStore, UserService userService, MessageSource messageSource) {
        this.tokenStore = tokenStore;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @ApiOperation(value = "Get Current User Info")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success")
    })
    @PreAuthorize("#oauth2.hasAnyScope('read')")
    @GetMapping("/me")
    public ResponseEntity getMyInfo(Locale locale) {
        final Authentication auth = getCurrentUserAuthentication();
        final OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) auth.getDetails();
        final OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(oauthDetails.getTokenValue());
        Map<String, Object> details = new HashMap<>();
        details.put("ip_address", oauthDetails.getRemoteAddress());
        if (auth instanceof OAuth2Authentication) {
            details.put("client_id", ((OAuth2Authentication)auth).getOAuth2Request().getClientId());
            details.put("authorities", ((OAuth2Authentication)auth).getOAuth2Request().getAuthorities());
        }
        details.put("token_type", oauthDetails.getTokenType());
        details.put("token", oauthDetails.getTokenValue());
        details.put("refresh_token", oAuth2AccessToken.getRefreshToken());
        details.putAll(oAuth2AccessToken.getAdditionalInformation());
        details.put("scopes", oAuth2AccessToken.getScope());
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel(true, null, details));
        return new ResponseEntity<>(
                new ResponseModel(true, messageSource.getMessage("app.message.success", null, locale), details),
                HttpStatus.OK);
    }

    @ApiOperation(value = "Create User with role \"Admin\" by Admin user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, response = UserModel.class, message = "Success"),
            @ApiResponse(code = 400, response = ResponseModel.class, message = "Bad Request"),
            @ApiResponse(code = 500, response = ResponseModel.class, message = "Server Error.")
    })
    @PostMapping("/admin")
    @PreAuthorize("#oauth2.clientHasRole('ROLE_ADMIN')")
    public ResponseEntity createAdminUser(@Valid @RequestBody UserModel userModel, Locale locale) {
//            return ResponseEntity.status(HttpStatus.CREATED).body(
//                    new ResponseModel(true, null, userService.createAdmin(userModel)));
        return new ResponseEntity<>(
                new ResponseModel(true, messageSource.getMessage("app.message.success", null, locale), userService.createAdmin(userModel)),
                HttpStatus.CREATED);
    }

    @ApiOperation(value = "Add avatar to a admin profile.", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added avatar"),
            @ApiResponse(code = 401, message = "Un-Authorized access to API or User is not found.")
    }
    )
    @PostMapping(value = "/avatar",
            consumes = {"multipart/form-data", "multipart/mixed"},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @PreAuthorize("#oauth2.hasAnyScope('write')")
    public ResponseEntity addAvatarToProfile(@RequestPart("avatar") MultipartFile avatar, Locale locale) {
        return new ResponseEntity<>(
                new ResponseModel(true, messageSource.getMessage("app.message.add.avatar.profile", null, locale),
                        this.userService.addAvatarToProfile(avatar, getCurrentUser().getId(), locale)),
                HttpStatus.OK);
    }
}
