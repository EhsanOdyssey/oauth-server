package neo.ehsanodyssey.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import neo.ehsanodyssey.oauth.enums.Authority;
import neo.ehsanodyssey.oauth.validator.Phone;
import neo.ehsanodyssey.oauth.validator.UsernameCondition;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@UsernameCondition
@Getter
public final class UserModel implements Serializable {

    private final String username;
    @Email
    private final String email;
    @Phone
    @JsonProperty("phone_number")
    private final Object phoneNumber;
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final Set<Authority> authorities;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final boolean activated;

    public UserModel(String username, String email, Object phoneNumber,
                     String password, Set<Authority> authorities, boolean activated) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.authorities = authorities;
        this.activated = activated;
    }

    @Override
    public String toString() {
        return "UserModel {" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", authorities=" + authorities +
                ", activated=" + activated +
                '}';
    }
}
