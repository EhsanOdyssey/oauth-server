package neo.ehsanodyssey.oauth.controller;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import neo.ehsanodyssey.oauth.dto.CountryCodeModel;
import neo.ehsanodyssey.oauth.dto.LocaleModel;
import neo.ehsanodyssey.oauth.dto.ResponseModel;
import neo.ehsanodyssey.oauth.dto.StorageSettingModel;
import neo.ehsanodyssey.oauth.entity.StorageSetting;
import neo.ehsanodyssey.oauth.service.StorageSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import neo.ehsanodyssey.oauth.config.MvcConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Api(tags = "HELPER: Helper endpoints")
@RestController
@RequestMapping("/settings")
public class SettingController {

    private final StorageSettingService storageSettingService;
    private final MessageSource messageSource;

    public SettingController(StorageSettingService storageSettingService, MessageSource messageSource) {
        this.storageSettingService = storageSettingService;
        this.messageSource = messageSource;
    }

    @ApiOperation(value = "Get Supported Regions for Phone Numbers")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success")
    })
    @GetMapping("/phone/countrycodes")
    public ResponseEntity getCountryCodes(Locale locale) {
        List<CountryCodeModel> responseEntity = new ArrayList<>();
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        phoneNumberUtil.getSupportedRegions().parallelStream().sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .forEach(regionCode -> {
                    Locale regionLocal = new Locale("en", regionCode);
                    CountryCodeModel countryCode = new CountryCodeModel(
                            regionCode, regionLocal.getDisplayCountry(), phoneNumberUtil.getCountryCodeForRegion(regionCode));
                    responseEntity.add(countryCode);
                });
        return new ResponseEntity<>(new ResponseModel(true,
                messageSource.getMessage("app.message.success", null, locale), responseEntity), HttpStatus.OK);
    }

    @ApiOperation(value = "Get supported locales by the application")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success")
    })
    @GetMapping("/locale/all")
    public ResponseEntity getSupportedLocales(Locale locale) {
        List<LocaleModel> responseEntity = new ArrayList<>();
        MvcConfiguration.SUPPORTED_LOCALES.forEach(supportedLocale -> {
            LocaleModel model = new LocaleModel(supportedLocale.getDisplayCountry(), supportedLocale.getDisplayLanguage(),
                    supportedLocale.toLanguageTag(), supportedLocale.getScript(), supportedLocale.getISO3Country(), supportedLocale.getISO3Language());
            responseEntity.add(model);
        });
        return new ResponseEntity<>(new ResponseModel(true,
                messageSource.getMessage("app.message.success", null, locale), responseEntity), HttpStatus.OK);
    }

    @ApiOperation(value = "Create Storage Settings by Admin user")
    @ApiResponses(value = {
            @ApiResponse(code = 201, response = StorageSetting.class, message = "Success"),
            @ApiResponse(code = 400, response = ResponseModel.class, message = "Bad Request"),
            @ApiResponse(code = 500, response = ResponseModel.class, message = "Server Error.")
    })
    @PreAuthorize("#oauth2.clientHasAnyRole('ROLE_ADMIN')")
    @PostMapping("/storage")
    public ResponseEntity createClient(@Valid @RequestBody StorageSettingModel dto, Locale locale) {
        return new ResponseEntity<>(new ResponseModel(true,
                messageSource.getMessage("app.message.success", null, locale),
                this.storageSettingService.create(dto)), HttpStatus.CREATED);
    }
}
