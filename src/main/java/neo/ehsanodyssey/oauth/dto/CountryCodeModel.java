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
public final class CountryCodeModel implements Serializable {
    @JsonProperty("region_code")
    private final String regionCode;
    @JsonProperty("country_name")
    private final String countryName;
    @JsonProperty("country_code")
    private final Integer countryCode;

    public CountryCodeModel(String regionCode, String countryName, Integer countryCode) {
        this.regionCode = regionCode;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }
}
