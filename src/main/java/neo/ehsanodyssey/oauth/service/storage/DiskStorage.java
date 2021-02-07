package neo.ehsanodyssey.oauth.service.storage;

import neo.ehsanodyssey.oauth.entity.File;
import neo.ehsanodyssey.oauth.entity.StorageSetting;
import neo.ehsanodyssey.oauth.exception.StorageException;
import neo.ehsanodyssey.oauth.utils.FileUtils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Service
public class DiskStorage implements Storage<MultipartFile, ByteArrayResource> {

    private final MessageSource messageSource;

    public DiskStorage(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void store(MultipartFile inputStreamSource, String fileName, StorageSetting storageSetting) {
        final String fileExtension = FileUtils.getFileExtension(inputStreamSource);
        final String contentType = inputStreamSource.getContentType();

        try {
            if (inputStreamSource.getSize() > storageSetting.getMaxSize()) {
                throw new StorageException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("app.error.file.size.large",
                                new Object[] {inputStreamSource.getOriginalFilename()}, LocaleContextHolder.getLocale()));
            }
            if (!storageSetting.getAcceptedTypes().contains(fileExtension.toLowerCase())) {
                throw new StorageException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("app.error.file.content.type",
                                new Object[] {fileExtension + " | " + contentType}, LocaleContextHolder.getLocale()));
            }
            if (inputStreamSource.isEmpty()) {
                throw new StorageException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("app.error.file.empty.content",
                                new Object[] {inputStreamSource.getOriginalFilename()}, LocaleContextHolder.getLocale()));
            }
            if (fileName.contains("..")) {
                throw new StorageException(HttpStatus.BAD_REQUEST,
                        "cannot store file with relative path outside current directory.");
            }
            try (InputStream inputStream = inputStreamSource.getInputStream()) {
                Files.copy(inputStream, storageSetting.getStorageLocation(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (NullPointerException e) {
            throw new StorageException(HttpStatus.NOT_ACCEPTABLE,
                    messageSource.getMessage("app.error.file.unknown.type",
                            new Object[] {inputStreamSource.getOriginalFilename()}, LocaleContextHolder.getLocale()));
        } catch (IOException e) {
            throw new StorageException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ByteArrayResource read(File file) {
        Path path = file.getSetting().getStorageLocation(file.getPath());
        if (path.toFile().exists() && path.toFile().isFile()) {
            try {
                return new ByteArrayResource(Files.readAllBytes(path));
            } catch (IOException e) {
                throw new StorageException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            throw new StorageException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("app.error.file.not.found", null, LocaleContextHolder.getLocale()));
        }
    }

    @Override
    public void delete(File file) {
        Path path = file.getSetting().getStorageLocation(file.getPath());
        if (path.toFile().exists()) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new StorageException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            throw new StorageException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("app.error.file.not.found", null, LocaleContextHolder.getLocale()));
        }
    }
}
