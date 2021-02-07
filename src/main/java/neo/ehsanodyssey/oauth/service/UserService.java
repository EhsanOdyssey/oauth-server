package neo.ehsanodyssey.oauth.service;

import neo.ehsanodyssey.oauth.dto.ResponseModel;
import neo.ehsanodyssey.oauth.dto.UserModel;
import neo.ehsanodyssey.oauth.entity.File;
import neo.ehsanodyssey.oauth.entity.StorageSetting;
import neo.ehsanodyssey.oauth.entity.User;
import neo.ehsanodyssey.oauth.enums.Authority;
import neo.ehsanodyssey.oauth.exception.ResourceNotFoundException;
import neo.ehsanodyssey.oauth.exception.ResponseModelException;
import neo.ehsanodyssey.oauth.exception.StorageException;
import neo.ehsanodyssey.oauth.repository.StorageSettingRepository;
import neo.ehsanodyssey.oauth.repository.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FileOnDiskService fileOnDiskService;
    private final StorageSettingRepository storageSettingRepository;
    private final MessageSource messageSource;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, FileOnDiskService fileOnDiskService,
                       StorageSettingRepository storageSettingRepository, MessageSource messageSource) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.fileOnDiskService = fileOnDiskService;
        this.storageSettingRepository = storageSettingRepository;
        this.messageSource = messageSource;
    }

    public UserModel createAdmin(UserModel dto) {
        return toDto(createUser(dto, Authority.ROLE_ADMIN));
    }

    public User createUser(UserModel dto, Authority... authorities) {
        return createUser(dto, Arrays.asList(authorities));
    }

    public User createUser(UserModel dto, List<Authority> authorities) {
        User user = new User(
                new User.UserBuilder(
                        dto.getUsername(),
                        dto.getEmail(),
                        dto.getPhoneNumber(),
                        passwordEncoder.encode(dto.getPassword()),
                        authorities.parallelStream().map(Authority::getAuthority).collect(Collectors.toSet())
                )
        );
        // TODO send sms or email with code for activation
        // TODO other routines on account creation
        return userRepository.save(user);
    }

    public User addAvatarToProfile(MultipartFile file, String userId, Locale locale) {
        String message = messageSource.getMessage("app.error.wrong.user.id", new Object[] {userId}, locale);
        User user = this.userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(HttpStatus.NOT_FOUND, new ResponseModel(false, message)));
        File avatarFile = null;
        Optional<StorageSetting> avatarSettings = this.storageSettingRepository.findAll().parallelStream()
                .filter(storageSetting -> storageSetting.getType().equalsIgnoreCase("avatar")).findAny();
        if (avatarSettings.isEmpty()) {
            throw new StorageException(HttpStatus.NOT_ACCEPTABLE,
                    messageSource.getMessage("app.error.storage.setting.not.found",
                            new Object[] {
                                    messageSource.getMessage("app.error.storage.setting.not.found.arg", null, locale)
                            }, locale));
        }
        avatarFile = this.fileOnDiskService.store(file, avatarSettings.get(), userId);
        if (avatarFile != null) {
            user.setAvatar(avatarFile);
            return this.userRepository.save(user);
        }
        throw new ResponseModelException(HttpStatus.INTERNAL_SERVER_ERROR,
                new ResponseModel(false, "Cannot store avatar."));
    }

    private UserModel toDto(User entity) {
        return new UserModel(
                entity.getUsername(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getPassword(),
                entity.getAuthorities().parallelStream()
                        .map(GrantedAuthority::getAuthority)
                        .map(Authority::valueOf)
                        .collect(Collectors.toSet()),
                entity.isEnabled());
    }
}
