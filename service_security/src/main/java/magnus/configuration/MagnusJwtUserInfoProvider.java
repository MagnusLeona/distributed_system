package magnus.configuration;

import magnus.utils.jwt.JwtAuthentication;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class MagnusJwtUserInfoProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 如果已经认证了，就把客户相关信息放到redis里面。
        if (authentication.isAuthenticated()) {
            // todo 放到redis，或者内存中
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthentication.class);
    }
}
