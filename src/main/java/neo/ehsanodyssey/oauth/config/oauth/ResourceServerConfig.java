package neo.ehsanodyssey.oauth.config.oauth;

import neo.ehsanodyssey.oauth.config.handler.CustomAccessDeniedHandler;
import neo.ehsanodyssey.oauth.config.handler.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/oauth/validate/**").authenticated()
                .antMatchers("/oauth/check_token/**").authenticated()
                .antMatchers("/oauth/gettokens/**").authenticated()
                .antMatchers("/oauth/revoke/**").authenticated()
                .antMatchers("/oauth/clients/**").authenticated()
                .antMatchers("/users/**").authenticated()
                .antMatchers("/helper/**").permitAll()
                .antMatchers("/").permitAll()
                .and().csrf().disable();
    }

    @Override
    public void configure(final ResourceServerSecurityConfigurer config) {
        config
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
}
