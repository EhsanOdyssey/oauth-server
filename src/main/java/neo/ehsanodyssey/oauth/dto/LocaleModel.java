package neo.ehsanodyssey.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Getter
public final class LocaleModel implements Serializable {
    private final String country;
    private final String language;
    @JsonProperty("language_tag")
    private final String languageTag;
    private final String script;
    @JsonProperty("iso3_country")
    private final String iso3Country;
    @JsonProperty("iso3_language")
    private final String iso3Language;

    public LocaleModel(String country, String language, String languageTag, String script,
                       String iso3Country, String iso3Language) {
        this.country = country;
        this.language = language;
        this.languageTag = languageTag;
        this.script = script;
        this.iso3Country = iso3Country;
        this.iso3Language = iso3Language;
    }
}
