package neo.ehsanodyssey.oauth.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import neo.ehsanodyssey.oauth.enums.AuthProvider;
import neo.ehsanodyssey.oauth.enums.Authority;
import neo.ehsanodyssey.oauth.exception.CustomParseException;
import neo.ehsanodyssey.oauth.utils.ExceptionUtils;
import neo.ehsanodyssey.oauth.utils.PhoneUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.i18n.phonenumbers.PhoneNumberUtil.*;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Document(collection = "users")
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @Field(order = 0)
    private String id;
    @Field(order = 1)
    private String username;
    @Email
    @Field(order = 2)
    private String email;
    @Field(value = "phone_number", order = 3)
    private String phoneNumber;
    @NotEmpty
    @Field(order = 4)
    private String password;
    @NotEmpty
    @Field(order = 5)
    private String authorities;
    @Field(order = 6)
    private AuthProvider provider;
    @Field(value = "account_non_expired", order = 7)
    private boolean accountNonExpired;
    @Field(value = "account_non_locked", order = 8)
    private boolean accountNonLocked;
    @Field(value = "credentials_non_expired", order = 9)
    private boolean credentialsNonExpired;
    @Field(order = 10)
    private boolean enabled;
    @Field(order = 11)
    private File avatar;

    public User() {
    }

    public User(UserBuilder builder) {
        this.username = builder.username;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.password = builder.password;
        this.authorities = builder.authorities;
        this.accountNonExpired = builder.accountNonExpired;
        this.credentialsNonExpired = builder.credentialsNonExpired;
        this.accountNonLocked = builder.accountNonLocked;
        this.enabled = builder.enabled;
        this.provider = builder.provider;
        this.avatar = builder.avatar;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return StringUtils.commaDelimitedListToSet(authorities).parallelStream()
                .map(Authority::valueOf).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User {" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", authorities=" + authorities +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                '}';
    }

    public static class UserBuilder {
        private String username;
        private String email;
        private String phoneNumber;
        private String password;
        private String authorities;
        private boolean accountNonExpired = true;
        private boolean accountNonLocked = true;
        private boolean credentialsNonExpired = true;
        private boolean enabled = true;
        private AuthProvider provider = AuthProvider.SYSTEM;
        private File avatar;

        public UserBuilder(String username, String email, Object phoneNumber, String password,
                           Set<String> authorities) {
            if ((StringUtils.isEmpty(username) && StringUtils.isEmpty(email) &&
                    StringUtils.isEmpty(phoneNumber)) ||
                    StringUtils.isEmpty(password) || authorities == null || authorities.isEmpty()) {
                throw new IllegalArgumentException("app.error.null.mandatory.fields.constructor");
            }
            if (StringUtils.hasText(username)) {
                this.username = username;
            }
            if (StringUtils.hasText(email)) {
                this.email = email;
            }
            if (!StringUtils.isEmpty(phoneNumber)) {
                String formattedPhoneNumber = null;
                if (phoneNumber instanceof String) {
                    formattedPhoneNumber = PhoneUtils.getFormattedPhoneNumber((String) phoneNumber, PhoneNumberFormat.E164);
                } else if (phoneNumber instanceof PhoneNumber) {
                    formattedPhoneNumber =
                            PhoneUtils.getFormattedPhoneNumber(((PhoneNumber) phoneNumber).getValue(),
                                    ((PhoneNumber) phoneNumber).getRegionCode(), PhoneNumberFormat.E164);
                }
                if (formattedPhoneNumber == null) {
                    throw new CustomParseException("phone_number", ExceptionUtils.APP_ERROR_PARSE_ATTRIBUTE);
                }
                this.phoneNumber = formattedPhoneNumber;
            }
            this.password = password;
            try {
                Set<Authority> authoritySet = authorities.parallelStream()
                        .map(String::toUpperCase).map(Authority::valueOf).collect(Collectors.toSet());
                this.authorities = !authoritySet.isEmpty() ? sortAuthorities(authoritySet).parallelStream()
                        .map(Authority::toString).collect(Collectors.joining(",")) : null;
            } catch (IllegalArgumentException e) {
                throw new CustomParseException("authorities", ExceptionUtils.APP_ERROR_PARSE_ATTRIBUTE);
            }
        }

        public UserBuilder accountExpired(boolean enabled) {
            this.accountNonExpired = !enabled;
            return this;
        }

        public UserBuilder accountLocked(boolean enabled) {
            this.accountNonLocked = !enabled;
            return this;
        }

        public UserBuilder credentialsExpired(boolean enabled) {
            this.credentialsNonExpired = !enabled;
            return this;
        }

        public UserBuilder inactive(boolean enabled) {
            this.enabled = !enabled;
            return this;
        }

        public UserBuilder withProvider(AuthProvider provider) {
            this.provider = provider;
            return this;
        }

        public UserBuilder withAvatar(File avatar) {
            this.avatar = avatar;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Getter
    @ToString
    public static final class PhoneNumber implements Serializable {
        @NotEmpty
        private final String value;
        @JsonProperty("region_code")
        private final String regionCode;

        public PhoneNumber(@NotEmpty String value, String regionCode) {
            this.value = value;
            this.regionCode = StringUtils.hasText(regionCode) ? regionCode : PhoneUtils.DEFAULT_REGION_CODE;
        }
    }

    private static SortedSet<Authority> sortAuthorities(Set<Authority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        SortedSet<Authority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());
        for (Authority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority, "Authority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }
        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<Authority>, Serializable {
        public int compare(Authority g1, Authority g2) {
            return g1.ordinal() - g2.ordinal();
        }
    }

    private static String parseAndFormatPhoneNumber(String phoneNumber, String region,
                                            PhoneNumberFormat formatType) throws NumberParseException {
        if (StringUtils.hasText(phoneNumber)) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber parsedPhone = PhoneNumberUtil.getInstance()
                    .parse(phoneNumber, (StringUtils.hasText(region) ? region : "IR"));
            if (phoneUtil.isValidNumber(parsedPhone)) {
                return phoneUtil.format(parsedPhone, (formatType != null ? formatType : PhoneNumberFormat.E164));
            }
        }
        return null;
    }
}
