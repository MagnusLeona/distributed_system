package magnus.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.MediaType;
import lombok.Setter;
import magnus.utils.jwt.JwtAuthentication;
import magnus.utils.jwt.JwtPayload;
import magnus.utils.jwt.JwtTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Jwt授权，判断当前路径是否是/login POST,如果满足条件则给予授权服务
 */
@Setter
public class MagnusJwtAuthorizationFilter extends AbstractAuthenticationProcessingFilter {

    private String tokenHeader;
    private ObjectMapper objectMapper;
    private JwtTokenService jwtTokenService;

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login",
                                                                                                            HttpMethod.POST);
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    protected MagnusJwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    protected MagnusJwtAuthorizationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // 取 登录请求的数据
        if (!request.getMethod().equals(HttpMethod.POST)) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        ServletInputStream inputStream = request.getInputStream();
        byte[] b = new byte[1024];
        int read;
        StringBuilder stringBuilder = new StringBuilder();
        while ((read = inputStream.read(b)) > 0) {
            stringBuilder.append(new String(b, 0, read));
        }
        String reqBody = stringBuilder.toString();
        Map map = objectMapper.readValue(reqBody, Map.class);
        //
        String username = (String) map.get(USERNAME_KEY);
        String password = (String) map.get(PASSWORD_KEY);
//        JwtAuthentication unauthenticated = (JwtAuthentication) JwtAuthentication.unauthenticated(username, password);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                username, password);
        this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
        return usernamePasswordAuthenticationToken;
    }

    public void initialize() {
        // todo 设置success handler 和 failure handler
        super.setAuthenticationSuccessHandler(((request, response, authentication) -> {
            JwtPayload build = JwtPayload.builder().expireTime(Instant.now().plusSeconds(300).toEpochMilli())
                                         .userid((String) authentication.getPrincipal())
                                         .allocationTime(Instant.now().toEpochMilli()).build();
            String token = jwtTokenService.generateToken(build);
            // 需要生成唯一的id吗？唯一id的作用是什么？根据id存储用户登录信息。
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;
            usernamePasswordAuthenticationToken.setDetails(build);
            response.setContentType(MediaType.APPLICATION_JSON);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            Map<String, Object> map = new HashMap<>();
            map.put("result", "登录成功");
            map.put("authentication", authentication);
            response.getWriter().write(objectMapper.writeValueAsString(map));
            response.setHeader(tokenHeader, token);
        }));

        super.setAuthenticationFailureHandler(((request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }));
    }
}
