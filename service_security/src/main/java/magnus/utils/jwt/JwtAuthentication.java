package magnus.utils.jwt;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class JwtAuthentication extends UsernamePasswordAuthenticationToken {
    private String token;

    public JwtAuthentication(Object username, Object password) {
        super(username, password);
    }

    public void setToken(String token) {
        this.token = token;
    }

}
