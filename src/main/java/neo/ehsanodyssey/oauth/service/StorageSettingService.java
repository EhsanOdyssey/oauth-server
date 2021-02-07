package neo.ehsanodyssey.oauth.service;

import neo.ehsanodyssey.oauth.config.AppProperties;
import neo.ehsanodyssey.oauth.dto.ResponseModel;
import neo.ehsanodyssey.oauth.dto.StorageSettingModel;
import neo.ehsanodyssey.oauth.entity.StorageSetting;
import neo.ehsanodyssey.oauth.exception.ResourceNotFoundException;
import neo.ehsanodyssey.oauth.repository.StorageSettingRepository;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Service
public class StorageSettingService {

    private final StorageSettingRepository storageSettingRepository;
    private final AppProperties appProperties;
    private final MessageSource messageSource;

    public StorageSettingService(StorageSettingRepository storageSettingRepository, AppProperties appProperties,
                                 MessageSource messageSource) {
        this.storageSettingRepository = storageSettingRepository;
        this.appProperties = appProperties;
        this.messageSource = messageSource;
    }

    public StorageSetting create(StorageSettingModel dto) {
        return this.storageSettingRepository.save(
                new StorageSetting(dto.getType(), appProperties.getFiles().getBasePath() + dto.getPath(), dto.getMaxSize(), dto.getAcceptedTypes()));
    }

    public StorageSetting update(StorageSettingModel dto, Locale locale) {
        StorageSetting storageSetting = this.storageSettingRepository.findByType(dto.getType());
        if (storageSetting != null) {
            return this.storageSettingRepository.save(
                    new StorageSetting(dto.getType(), appProperties.getFiles().getBasePath() + dto.getPath(), dto.getMaxSize(), dto.getAcceptedTypes()));
        }
        throw new ResourceNotFoundException(HttpStatus.NOT_FOUND,
                new ResponseModel(false, messageSource.getMessage("app.error.storage.setting.not.found",
                        new Object[] {
                                messageSource.getMessage("app.error.storage.setting.not.found.arg", null, locale)
                        }, locale)));
    }
}
