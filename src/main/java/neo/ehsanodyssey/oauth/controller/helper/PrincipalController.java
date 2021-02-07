package neo.ehsanodyssey.oauth.controller.helper;

import neo.ehsanodyssey.oauth.entity.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author <a href="mailto:ehsan.odyssey@gmail.com">EhsanOdyssey</a>
 * @project oauth-server
 * @date Mon 10 Dec 2019
 */
public abstract class PrincipalController {

    protected Authentication getCurrentUserAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    protected Object getCurrentUserPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth.getPrincipal();
    }

    protected User getCurrentUser() {
        Object userPrincipal = getCurrentUserPrincipal();
        if (userPrincipal instanceof User) {
            return (User) userPrincipal;
        }
        return null;
    }
}
