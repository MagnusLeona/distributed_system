package magnus.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import lombok.Setter;
import magnus.resources.exception.AuthenticationFailedException;
import magnus.utils.jwt.JwtTokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Setter
public class MagnusUsernamePasswordFilter extends UsernamePasswordAuthenticationFilter {

    String tokenHeader;
    ObjectMapper objectMapper;
    JwtTokenService jwtTokenService;

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        // 自定义Json格式的登录报文处理
        // 如果content-type为json
        if (requireAuthentication(request, response)) {
            // 使用JWT认证
            ServletInputStream inputStream;
            try {
                inputStream = request.getInputStream();
                byte[] b = new byte[1024];
                int read;
                StringBuilder stringBuilder = new StringBuilder();
                while ((read = inputStream.read(b)) > 0) {
                    stringBuilder.append(new String(Arrays.copyOfRange(b, 0, read)));
                }
                String s = stringBuilder.toString();
                Map paramMap = objectMapper.readValue(s, Map.class);
                String username = (String) paramMap.get(this.getUsernameParameter());
                String password = (String) paramMap.get(this.getPasswordParameter());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        username, password);
                return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new AuthenticationFailedException("authentication failed");
    }

    public boolean requireAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (request.getContentType().equals(MediaType.APPLICATION_JSON)) {
            return true;
        }
        return false;
    }

    public void initialize() {
        this.setSecurityContextRepository(
                new DelegatingSecurityContextRepository(new HttpSessionSecurityContextRepository(),
                                                        new RequestAttributeSecurityContextRepository()));
        this.setAuthenticationSuccessHandler((request, response, authentication) -> {
            response.setContentType(MediaType.APPLICATION_JSON);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            Map<String, Object> map = new HashMap<>();
            map.put("result", "登录成功");
            map.put("authentication", authentication);
            response.getWriter().write(objectMapper.writeValueAsString(map));
        });
        this.setAuthenticationFailureHandler((request, response, exception) -> {
            response.setContentType(MediaType.APPLICATION_JSON);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            Map<String, String> map = new HashMap<>();
            map.put("result", "登录失败");
            map.put("authentication", exception.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(map));
        });
    }
}
