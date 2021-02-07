package neo.ehsanodyssey.oauth.service;

import neo.ehsanodyssey.oauth.dto.ResponseModel;
import neo.ehsanodyssey.oauth.entity.File;
import neo.ehsanodyssey.oauth.entity.StorageSetting;
import neo.ehsanodyssey.oauth.exception.ResourceNotFoundException;
import neo.ehsanodyssey.oauth.repository.FileRepository;
import neo.ehsanodyssey.oauth.repository.StorageSettingRepository;
import neo.ehsanodyssey.oauth.service.storage.DiskStorage;
import neo.ehsanodyssey.oauth.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Service
public class FileOnDiskService {

    private static final Logger logger = LoggerFactory.getLogger(FileOnDiskService.class);
    private final FileRepository fileRepository;
    private final StorageSettingRepository storageSettingRepository;
    private final DiskStorage diskStorage;
    private final MessageSource messageSource;

    public FileOnDiskService(FileRepository fileRepository, StorageSettingRepository storageSettingRepository,
                             DiskStorage diskStorage, MessageSource messageSource) {
        this.fileRepository = fileRepository;
        this.storageSettingRepository = storageSettingRepository;
        this.diskStorage = diskStorage;
        this.messageSource = messageSource;
    }

    @PostConstruct
    private void init() {
        logger.info("INIT method called");
        List<StorageSetting> settings = this.storageSettingRepository.findAll();
        settings.parallelStream().forEach(setting -> {
            Path path = Paths.get(setting.getPath());
            if (!path.toFile().exists()) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    logger.error(e.toString());
                }
            }
        });
    }

    public File store(final MultipartFile multipartFile, final StorageSetting storageSetting, final String userId) {
        final String fileExtension = FileUtils.getFileExtension(multipartFile);
        final String filename = FileUtils.fileNameGenerator(userId, fileExtension);
        final String contentType = multipartFile.getContentType();

        File file = new File();
        file.setSetting(storageSetting);
        file.setPath(filename);
        file.setContentType(contentType);
        this.diskStorage.store(multipartFile, filename, storageSetting);
        return fileRepository.save(file);
    }

    public ResponseEntity read(Long fileId, Locale locale) {
        Optional<File> optionalFile = fileRepository.findById(fileId);
        if (optionalFile.isPresent()) {
            Path path = optionalFile.get().getSetting().getStorageLocation(optionalFile.get().getPath());
            ByteArrayResource resource = this.diskStorage.read(optionalFile.get());
            return ResponseEntity.ok().
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
                    .contentType(MediaType.parseMediaType(optionalFile.get().getContentType()))
                    .body(resource);
        } else {
            throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST,
                    new ResponseModel(false, messageSource.getMessage("app.error.file.not.found", null, locale)));
        }
    }

    public void delete(Long fileId, Locale locale) {
        Optional<File> optionalFile = fileRepository.findById(fileId);
        if (optionalFile.isPresent()) {
            fileRepository.delete(optionalFile.get());
            this.diskStorage.delete(optionalFile.get());
        } else {
            throw new ResourceNotFoundException(HttpStatus.BAD_REQUEST,
                    new ResponseModel(false, messageSource.getMessage("app.error.file.not.found", null, locale)));
        }
    }
}