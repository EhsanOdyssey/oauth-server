package neo.ehsanodyssey.oauth.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public class FileUtils {

    public static String getFileExtension(final MultipartFile file) {
        if (StringUtils.hasText(file.getOriginalFilename())) {
            final String[] split = file.getOriginalFilename().split("\\.");
            return split[split.length - 1];
        }
        return "";
    }

    public static String fileNameGenerator(String userId, String extension) {
        final Instant now = Instant.now();
        return String.format("user-%s-%s.%s", userId, now.toEpochMilli(), extension);
    }
}
